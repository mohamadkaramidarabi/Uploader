package ir.sharif.drive.uploader.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import ir.sharif.drive.uploader.storage.TokenStorage
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    return HttpClient(Js) {
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
        }
    }
}

actual fun getTokenStorage(): TokenStorage {
    return TokenStorage()
}

