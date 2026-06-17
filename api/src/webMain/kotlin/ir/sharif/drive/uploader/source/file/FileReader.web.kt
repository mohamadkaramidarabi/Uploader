package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.models.FilePath
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import org.khronos.webgl.ArrayBuffer
import org.w3c.files.Blob
import kotlin.js.ExperimentalWasmJsInterop
import org.w3c.files.FileReader as BrowserFileReader

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileReader {
    actual suspend fun readChunk(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long,
    ): ByteArray? {
        val blob = WebFileRegistry.get(filePath.value) ?: WebFileRegistry.getCached(filePath.value) ?: return null
        return try {
            val fileSize = blob.browserSize()
            val offset = chunkIndex * chunkSize
            if (offset >= fileSize) return null
            val end = minOf(offset + chunkSize, fileSize)
            blob.slice(offset.toInt(), end.toInt()).readAsByteArray()
        } catch (_: Exception) {
            null
        }
    }
}

actual fun createFileReader(context: Any?): FileReader = FileReader()

@OptIn(ExperimentalWasmJsInterop::class)
private suspend fun Blob.readAsByteArray(): ByteArray =
    suspendCancellableCoroutine { continuation ->
        val reader = BrowserFileReader()
        reader.onload = {
            val buffer = reader.result as ArrayBuffer
            continuation.resume(buffer.toKotlinByteArray())
        }
        reader.onerror = {
            continuation.resumeWithException(Exception("Failed to read file chunk"))
        }
        reader.readAsArrayBuffer(this)
    }
