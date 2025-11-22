package ir.sharif.drive.uploader.source.file

import ir.sharif.drive.uploader.models.FilePath

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