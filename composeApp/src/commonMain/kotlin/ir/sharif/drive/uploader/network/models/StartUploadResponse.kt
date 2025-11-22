package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartUploadResponse(
    @SerialName("upload_id")
    val uploadId: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("signed_urls")
    val signedUrls: List<String> = emptyList(),
    @SerialName("chunk_size")
    val chunkSize: Long = -1L
)