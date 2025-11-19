package ir.sharif.drive.uploader.api

import io.ktor.client.HttpClient
import ir.sharif.drive.uploader.concrete.Uploader
import ir.sharif.drive.uploader.models.UploadRequest

interface IUploader {

    companion object {
        fun init(
            httpClient: HttpClient,
            startEndPoint: String,
            completeEndPoint: String
        ): IUploader = Uploader.apply {
            this.httpClient = httpClient
            this.startEndPoint = startEndPoint
            this.completeEndPoint = completeEndPoint
        }
    }
    suspend fun upload(
        requests: List<UploadRequest>
    )

}