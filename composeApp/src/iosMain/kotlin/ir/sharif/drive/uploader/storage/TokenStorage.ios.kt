package ir.sharif.drive.uploader.storage

import ir.sharif.drive.uploader.network.models.LoginResponseModel
import platform.Foundation.NSUserDefaults

actual class TokenStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual suspend fun saveTokens(loginResponse: LoginResponseModel) {
        loginResponse.access?.let { 
            userDefaults.setObject(it, forKey = "access_token")
        }
        loginResponse.refresh?.let { 
            userDefaults.setObject(it, forKey = "refresh_token")
        }
        loginResponse.tokenId?.let { 
            userDefaults.setObject(it, forKey = "token_id")
        }
        userDefaults.synchronize()
    }

    actual suspend fun getTokens(): LoginResponseModel? {
        val access = userDefaults.objectForKey("access_token") as? String ?: return null
        val refresh = userDefaults.objectForKey("refresh_token") as? String
        val tokenId = userDefaults.objectForKey("token_id") as? String
        
        return LoginResponseModel(
            access = access,
            refresh = refresh,
            tokenId = tokenId
        )
    }

    actual suspend fun clearTokens() {
        userDefaults.removeObjectForKey("access_token")
        userDefaults.removeObjectForKey("refresh_token")
        userDefaults.removeObjectForKey("token_id")
        userDefaults.synchronize()
    }
}

