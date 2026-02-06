package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.concrete.Uploader
import ir.sharif.drive.uploader.models.CompleteUploadRequest
import ir.sharif.drive.uploader.models.StartUploadResponse
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo
import ir.sharif.drive.uploader.models.UploadRequest
import kotlinx.coroutines.flow.Flow

interface IUploader {

    companion object {
        fun init(
            startUpload: suspend (Long) -> StartUploadResponse,
            putChunk: suspend (String, ByteArray, Long) -> String,
            completeUpload: suspend (CompleteUploadRequest) -> Unit,
            fileReaderContext: Any,
        ): IUploader = Uploader.getInstance(startUpload, putChunk, completeUpload, fileReaderContext)
    }
    fun upload(
        requests: List<UploadRequest>
    )

    fun getUploadingByState(state: States.UploadInfo.State): Flow<List<UploadInfo>>
    fun getAllUploadInfos(): Flow<List<UploadInfo>>

    suspend fun pause(id: Long)
    suspend fun resume(id: Long)
    suspend fun cancel(id: Long)

    suspend fun deleteAll()

}