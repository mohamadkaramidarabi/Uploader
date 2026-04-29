package ir.sharif.drive.uploader.storage

import ir.sharif.drive.uploader.network.models.LoginResponseModel

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class TokenStorage {
    suspend fun saveTokens(loginResponse: LoginResponseModel)
    suspend fun getTokens(): LoginResponseModel?
    suspend fun clearTokens()
}

