package ir.sharif.drive.uploader.di

import ir.sharif.drive.uploader.LoginViewModel
import ir.sharif.drive.uploader.main.MainViewModel
import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.network.AuthApiImpl
import ir.sharif.drive.uploader.network.UploadApi
import ir.sharif.drive.uploader.network.UploadApiImpl
import ir.sharif.drive.uploader.network.createApiClient
import ir.sharif.drive.uploader.storage.TokenStorage
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun provideTokenStorage(): TokenStorage

val appModule = module {
    single {
        Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    }
    val baseUrl = "https://abrehamrahi.ir"
    single { createApiClient(baseUrl, get()) }
    single {
        AuthApiImpl(httpClient = get())
    } bind AuthApi::class
    
    single {
        UploadApiImpl(httpClient = get())
    } bind UploadApi::class
    
    single { provideTokenStorage() }
 
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
}

