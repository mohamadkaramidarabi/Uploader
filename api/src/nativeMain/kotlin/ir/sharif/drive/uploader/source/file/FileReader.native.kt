package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.models.FilePath

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileReader {
    actual suspend fun readChunk(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long
    ): ByteArray? {
        TODO("Not yet implemented")
    }
}

actual fun createFileReader(context: Any?): FileReader {
    TODO("Not yet implemented")
}