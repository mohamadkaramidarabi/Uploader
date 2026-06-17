@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.sharif.drive.uploader.cache.indexeddb

import ir.sharif.drive.uploader.cache.entity.WebLinkEntity
import ir.sharif.drive.uploader.cache.entity.WebUploadEntity
import ir.sharif.drive.uploader.models.States
import kotlin.js.JsAny
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.files.Blob
import org.w3c.files.File
import kotlin.js.ExperimentalWasmJsInterop

@Serializable
internal data class StoredUpload(
    val id: Long,
    val serverId: String? = null,
    val key: String? = null,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val chunkSize: Long? = null,
    val chunkCount: Int? = null,
    val state: String,
    val folderId: String? = null,
    val versionGroup: String? = null,
    val cloudPath: String,
    val metaData: String? = null,
)

@Serializable
internal data class StoredLink(
    val id: Long,
    val uploadId: Long,
    val eTag: String? = null,
    val state: String,
    val retryCount: Int,
    val chunkSize: Long,
    val url: String,
)

@Serializable
internal data class StoredBlobInfo(
    val path: String,
    val fileName: String,
)

internal val indexedDbJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal fun WebUploadEntity.toStored(): StoredUpload = StoredUpload(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = state.name,
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)

internal fun StoredUpload.toEntity(): WebUploadEntity = WebUploadEntity(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = States.UploadInfo.State.valueOf(state),
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)

internal fun WebLinkEntity.toStored(): StoredLink = StoredLink(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = state.name,
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)

internal fun StoredLink.toEntity(): WebLinkEntity = WebLinkEntity(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = States.Link.State.valueOf(state),
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)

private const val DB_NAME = "uploader-db"
private const val DB_VERSION = 1

private var database: JsAny? = null

internal object IndexedDbStore {
    private fun db(): JsAny = database ?: error("IndexedDB is not open")

    suspend fun open() {
        if (database != null) return
        database = idbOpen(DB_NAME, DB_VERSION).await()
    }

    suspend fun loadUploads(): List<WebUploadEntity> {
        open()
        return decodePayloads<StoredUpload>(idbLoadJsonRecords(db(), "uploads").await().asJsonString()).map { it.toEntity() }
    }

    suspend fun loadLinks(): List<WebLinkEntity> {
        open()
        return decodePayloads<StoredLink>(idbLoadJsonRecords(db(), "links").await().asJsonString()).map { it.toEntity() }
    }

    suspend fun saveUpload(upload: WebUploadEntity) {
        open()
        idbSaveJsonRecord(
            db(),
            "uploads",
            upload.id.toDouble(),
            indexedDbJson.encodeToString(upload.toStored()),
        ).await()
    }

    suspend fun saveLink(link: WebLinkEntity) {
        open()
        idbSaveJsonRecord(
            db(),
            "links",
            link.id.toDouble(),
            indexedDbJson.encodeToString(link.toStored()),
        ).await()
    }

    suspend fun deleteLink(linkId: Long) {
        open()
        idbDeleteRecord(db(), "links", linkId.toDouble()).await()
    }

    suspend fun clearUploadsAndLinks() {
        open()
        idbClearStore(db(), "uploads").await()
        idbClearStore(db(), "links").await()
    }

    suspend fun saveFile(path: String, fileName: String, file: File) {
        open()
        idbSaveFile(db(), path, fileName, file).await()
    }

    suspend fun loadFileBlob(path: String): Blob? {
        open()
        return idbLoadFileBlob(db(), path).await() as? Blob
    }

    suspend fun loadAllFileInfos(): List<StoredBlobInfo> {
        open()
        return indexedDbJson.decodeFromString(idbLoadFileInfos(db()).await().asJsonString())
    }

    suspend fun clearFiles() {
        open()
        idbClearStore(db(), "files").await()
    }

    suspend fun getMetaLong(key: String, default: Long): Long {
        open()
        return idbLoadMeta(db(), key).await().asDouble()?.toLong() ?: default
    }

    suspend fun saveMetaLong(key: String, value: Long) {
        open()
        idbSaveMeta(db(), key, value.toDouble()).await()
    }

    private inline fun <reified T> decodePayloads(jsonArray: String): List<T> {
        val payloads = indexedDbJson.decodeFromString<List<String>>(jsonArray)
        return payloads.map { indexedDbJson.decodeFromString<T>(it) }
    }

    private fun JsAny?.asJsonString(): String = this?.toString() ?: "[]"

    private fun JsAny?.asDouble(): Double? = this?.toString()?.toDoubleOrNull()
}
