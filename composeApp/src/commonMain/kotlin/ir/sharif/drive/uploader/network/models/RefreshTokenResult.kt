package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResult(
    @SerialName("access")
    val access: String? = null
)

