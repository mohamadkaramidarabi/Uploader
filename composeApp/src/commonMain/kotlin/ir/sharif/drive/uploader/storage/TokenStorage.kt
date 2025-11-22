package ir.sharif.drive.uploader.storage

import ir.sharif.drive.uploader.network.models.LoginResponseModel

expect class TokenStorage {
    suspend fun saveTokens(loginResponse: LoginResponseModel)
    suspend fun getTokens(): LoginResponseModel?
    suspend fun clearTokens()
}

