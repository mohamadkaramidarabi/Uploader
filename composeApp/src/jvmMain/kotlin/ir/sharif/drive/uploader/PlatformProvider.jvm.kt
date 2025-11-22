package ir.sharif.drive.uploader

import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.network.AuthApiImpl
import ir.sharif.drive.uploader.network.createHttpClient
import ir.sharif.drive.uploader.storage.TokenStorage

actual object PlatformProvider {
    private const val BASE_URL = "https://drive.abiu.ir"

}

