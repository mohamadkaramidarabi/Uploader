package ir.sharif.drive.uploader.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.headers
import io.ktor.utils.io.ByteReadChannel
import ir.sharif.drive.uploader.network.models.CompleteUploadRequest
import ir.sharif.drive.uploader.network.models.StartUploadRequest
import ir.sharif.drive.uploader.network.models.StartUploadResponse

class UploadApiImpl(
    private val httpClient: HttpClient,
) : UploadApi {

    override suspend fun startUpload(size: Long): StartUploadResponse {
        return httpClient.post(EndPoints.Upload.startUpload) {
            setBody(StartUploadRequest(size))
        }.body<StartUploadResponse>()
    }

    override suspend fun putChunk(url: String, chunkData: ByteArray, contentLength: Long): String? {
        return httpClient.put(url) {
            timeout {
                requestTimeoutMillis = 1000 * 60 * 1000
                connectTimeoutMillis = 1000 * 60 * 1000
            }
            headers {
                append("content-length", contentLength.toString())
            }
            setBody(ByteReadChannel(chunkData))
        }.headers["ETag"]
            ?.replace("\\", "")
            ?.replace("\"", "")
            ?.trim()
    }

    override suspend fun completeUpload(request: CompleteUploadRequest) {
        httpClient.post(EndPoints.Upload.completeUpload) {
            timeout {
                connectTimeoutMillis = 1000 * 60 * 1000
                requestTimeoutMillis = 1000 * 60 * 1000
                socketTimeoutMillis = 1000 * 60 * 1000
            }
            setBody(request)
        }.body<Unit>()
    }
}

