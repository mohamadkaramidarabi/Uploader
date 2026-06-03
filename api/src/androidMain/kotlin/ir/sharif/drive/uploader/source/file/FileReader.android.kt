package ir.sharif.drive.uploader.source.file

import android.content.Context
import android.net.Uri
import ir.sharif.drive.uploader.models.FilePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

actual class FileReader(private val context: Context?) {
    actual suspend fun readChunk(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val uri = filePath.value.toUri()
            context?.contentResolver?.openInputStream(uri)?.use { inputStream ->
                val offset = chunkIndex * chunkSize
                inputStream.skip(offset)
                val buffer = ByteArray(chunkSize.toInt())
                val bytesRead = inputStream.read(buffer)
                if (bytesRead > 0) {
                    if (bytesRead < buffer.size) {
                        buffer.copyOf(bytesRead)
                    } else {
                        buffer
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

actual fun createFileReader(context: Any?): FileReader = FileReader(context as? Context)
