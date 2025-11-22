package ir.sharif.drive.uploader.di

import ir.sharif.drive.uploader.PlatformProvider
import ir.sharif.drive.uploader.storage.TokenStorage

actual fun provideTokenStorage(): TokenStorage {
    val context = PlatformProvider.getContext()
        ?: throw IllegalStateException("Context not initialized. Call PlatformProvider.init(context) in MainActivity.onCreate()")
    return TokenStorage(context)
}

