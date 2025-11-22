package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseModel(
    @SerialName("access")
    val access: String? = null,
    @SerialName("refresh")
    val refresh: String? = null,
    @SerialName("token_id")
    val tokenId: String? = null,
    @SerialName("captcha_required")
    val captchaRequired: String? = null,
    @SerialName("is_registered")
    val isRegistered: Boolean = false,
)

