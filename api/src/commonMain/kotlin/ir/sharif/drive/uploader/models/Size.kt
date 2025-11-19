package ir.sharif.drive.uploader.models

import kotlin.jvm.JvmInline


@JvmInline
value class FileSize(val value: Long) {
    init {
        require(value > 0) { "Invalid file size: $value" }
    }
}

@JvmInline
value class ChunkSize(val value: Long) {
    init {
        require(value > 0) { "Invalid file size: $value" }
    }
}

@JvmInline
value class ChunkCount(val value: Int)

@JvmInline
value class FilePath(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "File name should not be empty"
        }
    }
}

@JvmInline
value class CloudPath(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "Invalid cloud path"
        }
    }
}

@JvmInline
value class FileName(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "File path name should not be empty"
        }
    }
}