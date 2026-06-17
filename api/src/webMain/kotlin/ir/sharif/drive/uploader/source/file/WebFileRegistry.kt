package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.cache.indexeddb.IndexedDbStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.w3c.files.Blob
import org.w3c.files.File

private const val WEB_FILE_PREFIX = "web-file://"
private const val META_NEXT_FILE_ID = "nextFileId"

object WebFileRegistry {
    private val blobs = mutableMapOf<String, Blob>()
    private val fileNames = mutableMapOf<String, String>()
    private var nextId = 1L
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var restored = false

    fun register(file: File): String {
        val path = "$WEB_FILE_PREFIX${nextId++}"
        blobs[path] = file
        fileNames[path] = file.name
        scope.launch {
            IndexedDbStore.saveFile(path, file.name, file)
            IndexedDbStore.saveMetaLong(META_NEXT_FILE_ID, nextId)
        }
        return path
    }

    suspend fun get(path: String): Blob? {
        ensureRestored()
        blobs[path]?.let { return it }
        return IndexedDbStore.loadFileBlob(path)?.also { blob ->
            blobs[path] = blob
        }
    }

    fun getCached(path: String): Blob? = blobs[path]

    fun getFileName(path: String): String? = fileNames[path]

    suspend fun restore() {
        if (restored) return
        IndexedDbStore.open()
        nextId = IndexedDbStore.getMetaLong(META_NEXT_FILE_ID, 1L)
        IndexedDbStore.loadAllFileInfos().forEach { info ->
            fileNames[info.path] = info.fileName
            IndexedDbStore.loadFileBlob(info.path)?.let { blob ->
                blobs[info.path] = blob
            }
        }
        val maxFromPaths = fileNames.keys
            .mapNotNull { it.removePrefix(WEB_FILE_PREFIX).toLongOrNull() }
            .maxOrNull()
            ?.plus(1)
        if (maxFromPaths != null) {
            nextId = maxOf(nextId, maxFromPaths)
        }
        restored = true
    }

    fun clear() {
        blobs.clear()
        fileNames.clear()
        nextId = 1L
        restored = false
    }

    fun isWebFilePath(path: String): Boolean = path.startsWith(WEB_FILE_PREFIX)

    private suspend fun ensureRestored() {
        if (!restored) {
            restore()
        }
    }
}
