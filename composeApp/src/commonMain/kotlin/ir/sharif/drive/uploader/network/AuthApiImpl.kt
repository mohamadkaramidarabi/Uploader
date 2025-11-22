package ir.sharif.drive.uploader.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import ir.sharif.drive.uploader.network.models.GenerateCodeModel
import ir.sharif.drive.uploader.network.models.GenerateCodeResponse
import ir.sharif.drive.uploader.network.models.LoginRequest
import ir.sharif.drive.uploader.network.models.LoginResponseModel

class AuthApiImpl(
    private val httpClient: HttpClient
) : AuthApi {
    
    override suspend fun generateCode(
        phone: String,
        captchaId: String?,
        captchaValue: String?,
        captchaProvider: String?,
        requestType: Int?
    ): GenerateCodeResponse {
        return httpClient.post(EndPoints.Authentication.generateCode) {
            setBody(
                GenerateCodeModel(
                    phoneNumber = phone.removePrefix("0"),
                    captchaId = captchaId,
                    captchaValue = captchaValue,
                    captchaProvider = captchaProvider,
                    requestType = requestType,
                    prefix = "+98"
                )
            )
        }.body()
    }

    override suspend fun login(
        phone: String,
        code: Int?,
        captchaId: String?,
        captchaValue: String?,
        captchaProvider: String?,
        password: String?
    ): LoginResponseModel {
        return httpClient.post {
            url(EndPoints.Authentication.login)
            setBody(
                LoginRequest(
                    phoneNumber = phone.removePrefix("0"),
                    password = password,
                    code = code,
                    captchaId = captchaId,
                    captchaValue = captchaValue,
                    captchaProvider = captchaProvider,
                    prefix = "+98",
                    country = "IR"
                )
            )
        }.body()
    }
}

