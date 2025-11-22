package ir.sharif.drive.uploader.network

import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel

object ApiLogger {
    object KtorLogger : Logger {
        override fun log(message: String) {
            println("Ktor: $message")
        }
    }
}

