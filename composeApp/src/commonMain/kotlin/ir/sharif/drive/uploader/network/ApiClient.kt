package ir.sharif.drive.uploader.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ir.sharif.drive.uploader.network.models.RefreshTokenResult
import ir.sharif.drive.uploader.storage.TokenStorage
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClientEngineFactory<*>
expect fun getTokenStorage(): TokenStorage

fun createApiClient(baseUrl: String, json: Json): HttpClient {
    val baseClient = createHttpClient()
    val tokenStorage = getTokenStorage()
    
    return HttpClient(baseClient) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = ApiLogger.KtorLogger
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            socketTimeoutMillis = 60000
            connectTimeoutMillis = 60000
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            url(baseUrl)
        }

        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val tokens = tokenStorage.getTokens()
                    val access = tokens?.access?.removePrefix("Bearer ")?.removePrefix("bearer ")
                    val refresh = tokens?.refresh
                    
                    if (access != null && refresh != null) {
                        BearerTokens(access, refresh)
                    } else if (access != null) {
                        BearerTokens(access, "")
                    } else {
                        null
                    }
                }
                
                refreshTokens {
                    val tokens = tokenStorage.getTokens()
                    val refreshToken = tokens?.refresh ?: return@refreshTokens BearerTokens("", "")

                    val result = runCatching {
                        client.post(EndPoints.Authentication.refreshToken) {
                            setBody(mapOf("refresh" to refreshToken))
                            markAsRefreshTokenRequest()
                        }.body<RefreshTokenResult>()
                    }
                    
                    result.getOrNull()?.let { refreshResult ->
                        val newAccess = refreshResult.access
                        if (newAccess != null) {
                            val updatedTokens = tokens.copy(access = newAccess)
                            tokenStorage.saveTokens(updatedTokens)
                            return@refreshTokens BearerTokens(newAccess, refreshToken)
                        }
                    }
                    
                    BearerTokens("", "")
                }
            }
        }
    }
}

