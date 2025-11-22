package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompleteUploadRequest(
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("parent")
    val parent: String?,
    @SerialName("upload_id")
    val uploadId: String,
    @SerialName("parts")
    val parts: List<Part>,
    @SerialName("force_overwrite")
    val forceOverwrite: Boolean? = null,
    @SerialName("version_group")
    val versionGroup: String? = null,
    @SerialName("is_hidden")
    val isHidden: Boolean = false,
)

@Serializable
data class Part(
    @SerialName("ETag")
    val eTag: String,
    @SerialName("PartNumber")
    val partNumber: Int
)

