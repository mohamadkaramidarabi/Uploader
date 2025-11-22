package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.models.FilePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileReader {
    actual suspend fun readChunk(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath.value)
            RandomAccessFile(file, "r").use { raf ->
                val offset = chunkIndex * chunkSize
                raf.seek(offset)
                val buffer = ByteArray(chunkSize.toInt())
                val bytesRead = raf.read(buffer)
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
            null
        }
    }
}

actual fun createFileReader(context: Any?): FileReader = FileReader()

