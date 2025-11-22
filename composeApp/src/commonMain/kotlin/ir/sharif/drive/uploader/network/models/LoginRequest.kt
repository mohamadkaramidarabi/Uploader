package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("phone")
    val phoneNumber: String?,
    @SerialName("password")
    val password: String?,
    @SerialName("code")
    val code: Int? = null,
    @SerialName("captcha_id")
    val captchaId: String? = null,
    @SerialName("captcha_value")
    val captchaValue: String? = null,
    @SerialName("captcha_provider")
    val captchaProvider: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("prefix")
    val prefix: String,
    @SerialName("country")
    val country: String,
)

