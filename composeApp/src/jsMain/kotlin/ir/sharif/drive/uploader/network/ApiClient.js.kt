package ir.sharif.drive.uploader.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js
import ir.sharif.drive.uploader.storage.TokenStorage

actual fun createHttpClient(): HttpClientEngineFactory<*> = Js

actual fun getTokenStorage(): TokenStorage = TokenStorage()
