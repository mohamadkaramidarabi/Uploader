package ir.sharif.drive.uploader.storage

import ir.sharif.drive.uploader.network.models.LoginResponseModel
import java.util.prefs.Preferences

actual class TokenStorage {
    private val prefs = Preferences.userRoot().node("uploader_token_storage")

    actual suspend fun saveTokens(loginResponse: LoginResponseModel) {
        loginResponse.access?.let { prefs.put("access_token", it) }
        loginResponse.refresh?.let { prefs.put("refresh_token", it) }
        loginResponse.tokenId?.let { prefs.put("token_id", it) }
        prefs.flush()
    }

    actual suspend fun getTokens(): LoginResponseModel? {
        val access = prefs.get("access_token", null) ?: return null
        val refresh = prefs.get("refresh_token", null)
        val tokenId = prefs.get("token_id", null)
        
        return LoginResponseModel(
            access = access,
            refresh = refresh,
            tokenId = tokenId
        )
    }

    actual suspend fun clearTokens() {
        prefs.remove("access_token")
        prefs.remove("refresh_token")
        prefs.remove("token_id")
        prefs.flush()
    }
}

