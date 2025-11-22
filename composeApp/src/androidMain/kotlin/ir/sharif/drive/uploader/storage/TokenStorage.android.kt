package ir.sharif.drive.uploader.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.sharif.drive.uploader.network.models.LoginResponseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_storage")

private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
private val TOKEN_ID_KEY = stringPreferencesKey("token_id")

actual class TokenStorage(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    actual suspend fun saveTokens(loginResponse: LoginResponseModel) {
        context.dataStore.edit { preferences ->
            loginResponse.access?.let { preferences[ACCESS_TOKEN_KEY] = it }
            loginResponse.refresh?.let { preferences[REFRESH_TOKEN_KEY] = it }
            loginResponse.tokenId?.let { preferences[TOKEN_ID_KEY] = it }
        }
    }

    actual suspend fun getTokens(): LoginResponseModel? {
        return context.dataStore.data.map { preferences ->
            val access = preferences[ACCESS_TOKEN_KEY]
            val refresh = preferences[REFRESH_TOKEN_KEY]
            val tokenId = preferences[TOKEN_ID_KEY]
            
            if (access != null) {
                LoginResponseModel(
                    access = access,
                    refresh = refresh,
                    tokenId = tokenId
                )
            } else {
                null
            }
        }.first()
    }

    actual suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(TOKEN_ID_KEY)
        }
    }
}

