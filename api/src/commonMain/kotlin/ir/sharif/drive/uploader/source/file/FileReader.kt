package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.models.FilePath

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileReader {
    suspend fun readChunk(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long
    ): ByteArray?
}

expect fun createFileReader(context: Any?): FileReader
