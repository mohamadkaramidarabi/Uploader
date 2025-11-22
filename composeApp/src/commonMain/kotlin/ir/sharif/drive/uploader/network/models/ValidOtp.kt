package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidOtp(
    @SerialName("phone") val phone: String,
    @SerialName("code") val code: Int,
    @SerialName("prefix") val prefix: String,
)

