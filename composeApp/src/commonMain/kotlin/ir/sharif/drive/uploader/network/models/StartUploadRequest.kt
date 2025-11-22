package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartUploadRequest(
    @SerialName("obj_size")
    val size: Long
)

