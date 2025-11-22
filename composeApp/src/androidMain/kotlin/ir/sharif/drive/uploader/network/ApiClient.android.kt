package ir.sharif.drive.uploader.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import ir.sharif.drive.uploader.PlatformProvider
import ir.sharif.drive.uploader.storage.TokenStorage

actual fun createHttpClient(): HttpClientEngineFactory<*>  = Android

actual fun getTokenStorage(): TokenStorage {
    val context = PlatformProvider.getContext()
        ?: throw IllegalStateException("Context not initialized")
    return TokenStorage(context)
}

