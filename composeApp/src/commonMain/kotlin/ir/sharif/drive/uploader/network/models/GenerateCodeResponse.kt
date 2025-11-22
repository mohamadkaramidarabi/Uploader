package ir.sharif.drive.uploader.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GenerateCodeResponse {
    @SerialName("captcha_required")
    val captchaRequired: String? = null
}

