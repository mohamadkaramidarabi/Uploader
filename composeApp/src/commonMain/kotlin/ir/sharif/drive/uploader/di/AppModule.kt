package ir.sharif.drive.uploader.di

import ir.sharif.drive.uploader.LoginViewModel
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.main.MainViewModel
import ir.sharif.drive.uploader.models.StartUploadResponse
import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.network.AuthApiImpl
import ir.sharif.drive.uploader.network.UploadApi
import ir.sharif.drive.uploader.network.UploadApiImpl
import ir.sharif.drive.uploader.network.createApiClient
import ir.sharif.drive.uploader.network.models.CompleteUploadRequest
import ir.sharif.drive.uploader.network.models.Part
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

    single<IUploader> {
        val uploadApi: UploadApi = get()
        IUploader.init(
            startUpload = { size, metaData ->
                val response = uploadApi.startUpload(size)
                StartUploadResponse(
                    uploadId = response.uploadId,
                    key = response.key,
                    chunkSize = response.chunkSize,
                    links = response.signedUrls,
                )
            },
            putChunk = { url, chunkData, contentLength ->
                uploadApi.putChunk(url, chunkData, contentLength) ?: ""
            },
            completeUpload = {
                val request = CompleteUploadRequest(
                    key = it.key,
                    name = it.name,
                    parent = it.parent,
                    uploadId = it.uploadId,
                    parts = it.parts.map { p -> Part(p.eTag, p.partNumber) },
                )
                uploadApi.completeUpload(request)
            },
            cancelUpload = { },
            fileReaderContext = fileReaderContextForUploader(),
        )
    }
    
    single { provideTokenStorage() }
 
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
}

