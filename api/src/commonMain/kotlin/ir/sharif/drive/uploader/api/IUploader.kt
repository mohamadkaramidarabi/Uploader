package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.concrete.Uploader
import ir.sharif.drive.uploader.models.CompleteUploadRequest
import ir.sharif.drive.uploader.models.StartUploadResponse
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo
import ir.sharif.drive.uploader.models.UploadNotificationSettings
import ir.sharif.drive.uploader.models.UploadRequest
import kotlinx.coroutines.flow.Flow

interface IUploader {

    companion object {
        fun init(
            startUpload: suspend (size: Long, metaData: String?) -> StartUploadResponse,
            putChunk: suspend (String, ByteArray, Long) -> String,
            completeUpload: suspend (CompleteUploadRequest) -> Unit,
            cancelUpload: suspend (UploadInfo) -> Unit,
            fileReaderContext: Any,
            uploadNotificationSettings: UploadNotificationSettings = platformUploadNotificationSettings(),
        ): IUploader = Uploader.getInstance(
            startUpload,
            putChunk,
            completeUpload,
            cancelUpload,
            fileReaderContext,
            uploadNotificationSettings,
        )
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

    suspend fun retry(id: Long)

}