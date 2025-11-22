package ir.sharif.drive.uploader.models

data class CompleteUploadRequest(
    val key: String,
    val name: String,
    val parent: String?,
    val uploadId: String,
    val parts: List<Part>,
    val forceOverwrite: Boolean? = null,
    val versionGroup: String? = null,
    val isHidden: Boolean = false,
) {
    data class Part(
        val eTag: String,
        val partNumber: Int
    )
}
