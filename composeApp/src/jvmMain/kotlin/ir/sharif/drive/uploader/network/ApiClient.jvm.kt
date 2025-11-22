package ir.sharif.drive.uploader.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ir.sharif.drive.uploader.storage.TokenStorage
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClientEngineFactory<*> = Java

actual fun getTokenStorage(): TokenStorage {
    return TokenStorage()
}

