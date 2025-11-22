package ir.sharif.drive.uploader.network

import ir.sharif.drive.uploader.network.models.GenerateCodeResponse
import ir.sharif.drive.uploader.network.models.LoginResponseModel

interface AuthApi {
    suspend fun generateCode(
        phone: String,
        captchaId: String? = null,
        captchaValue: String? = null,
        captchaProvider: String? = null,
        requestType: Int? = null
    ): GenerateCodeResponse

    suspend fun login(
        phone: String,
        code: Int? = null,
        captchaId: String? = null,
        captchaValue: String? = null,
        captchaProvider: String? = null,
        password: String? = null
    ): LoginResponseModel
}

