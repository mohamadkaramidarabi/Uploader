package ir.sharif.drive.uploader.concrete

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.models.UploadInfo
import ir.sharif.drive.uploader.models.UploadRequest

internal object Uploader : IUploader, ViewModel() {

    lateinit var httpClient: HttpClient
    lateinit var startEndPoint: String
    lateinit var completeEndPoint: String


    override suspend fun upload(
        requests: List<UploadRequest>
    ) {
        requests.map {
            UploadInfo(
                id =0,
                uploadId = null,
                key = null,
                name = it.fileName,
                path = it.filePath,
                size = it.fileSize,
                chunkSize = null,
                chunkCount = null,
                state = UploadInfo.State.IN_QUEUE,
                folderId = it.folderId,
                cloudPath = it.cloudPath,
                links = emptyList(),
                versionGroup = null,
            )
        }
    }
}