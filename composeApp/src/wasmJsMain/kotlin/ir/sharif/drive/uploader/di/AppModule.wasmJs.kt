package ir.sharif.drive.uploader.di

import ir.sharif.drive.uploader.storage.TokenStorage

actual fun provideTokenStorage(): TokenStorage {
    return TokenStorage()
}

