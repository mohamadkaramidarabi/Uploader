package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateCodeModel(
    @SerialName("phone")
    val phoneNumber: String? = null,
    @SerialName("captcha_id")
    val captchaId: String? = null,
    @SerialName("captcha_value")
    val captchaValue: String? = null,
    @SerialName("captcha_provider")
    val captchaProvider: String? = null,
    @SerialName("request_type")
    val requestType: Int? = null,
    @SerialName("prefix")
    val prefix: String,
)

