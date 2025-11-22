package ir.sharif.drive.uploader

import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.network.AuthApiImpl
import ir.sharif.drive.uploader.network.createHttpClient
import ir.sharif.drive.uploader.storage.TokenStorage

actual object PlatformProvider {
    private const val BASE_URL = "https://drive.abiu.ir"
    
    actual fun createAuthApi(): AuthApi {
        val httpClient = createHttpClient()
        return AuthApiImpl(httpClient, BASE_URL)
    }
    
    actual fun createTokenStorage(): TokenStorage {
        return TokenStorage()
    }
}

