package ir.sharif.drive.uploader.storage

import ir.sharif.drive.uploader.network.models.LoginResponseModel
import kotlinx.browser.localStorage

actual class TokenStorage {
    actual suspend fun saveTokens(loginResponse: LoginResponseModel) {
        loginResponse.access?.let { localStorage.setItem("access_token", it) }
        loginResponse.refresh?.let { localStorage.setItem("refresh_token", it) }
        loginResponse.tokenId?.let { localStorage.setItem("token_id", it) }
    }

    actual suspend fun getTokens(): LoginResponseModel? {
        val access = localStorage.getItem("access_token") ?: return null
        val refresh = localStorage.getItem("refresh_token")
        val tokenId = localStorage.getItem("token_id")
        
        return LoginResponseModel(
            access = access,
            refresh = refresh,
            tokenId = tokenId
        )
    }

    actual suspend fun clearTokens() {
        localStorage.removeItem("access_token")
        localStorage.removeItem("refresh_token")
        localStorage.removeItem("token_id")
    }
}

