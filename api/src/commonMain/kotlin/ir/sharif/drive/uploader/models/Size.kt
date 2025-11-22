package ir.sharif.drive.uploader.models

import kotlin.jvm.JvmInline


@JvmInline
value class FileSize(val value: Long) {
    init {
        require(value > 0) { "Invalid file size: $value" }
    }
    companion object {
        val Long?.fileSize: FileSize?
            get() = this?.fileSize
        val Long.fileSize: FileSize
            get() = FileSize(this)
    }
}

@JvmInline
value class ChunkSize(val value: Long) {
    init {
        require(value > 0) { "Invalid file size: $value" }
    }
    companion object {
        val Long?.chunkSize: ChunkSize?
            get() = this?.chunkSize

        val Long.chunkSize: ChunkSize
            get() = ChunkSize(this)
    }
}

@JvmInline
value class ChunkCount(val value: Int) {
    companion object {
        val Int?.chunkCount: ChunkCount?
            get() = this?.let { ChunkCount(it) }
    }
}

@JvmInline
value class FilePath(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "File name should not be empty"
        }
    }
    companion object {
        val String?.filePath: FilePath?
            get() = this?.filePath

        val String.filePath: FilePath
            get() = FilePath(this)
    }
}

@JvmInline
value class CloudPath(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "Invalid cloud path"
        }
    }
    companion object {
        val String?.cloudPath: CloudPath?
            get() = this?.cloudPath

        val String.cloudPath: CloudPath
            get() = CloudPath(this)
    }
}

@JvmInline
value class FileName(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "File path name should not be empty"
        }
    }

    companion object {
        val String?.fileName: FileName?
            get() = this?.fileName
        val String.fileName: FileName
            get() = FileName(this)
    }
}