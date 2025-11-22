package ir.sharif.drive.uploader.models

data class StartUploadResponse(
    val uploadId: String,
    val key: String,
    val chunkSize: Long,
    val links: List<String>
)