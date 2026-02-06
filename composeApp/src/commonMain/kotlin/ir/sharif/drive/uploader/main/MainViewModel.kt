package ir.sharif.drive.uploader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.models.CloudPath.Companion.cloudPath
import ir.sharif.drive.uploader.models.FileName.Companion.fileName
import ir.sharif.drive.uploader.models.FilePath.Companion.filePath
import ir.sharif.drive.uploader.models.FileSize.Companion.fileSize
import ir.sharif.drive.uploader.models.StartUploadResponse
import ir.sharif.drive.uploader.models.UploadRequest
import ir.sharif.drive.uploader.network.UploadApi
import ir.sharif.drive.uploader.network.models.CompleteUploadRequest
import ir.sharif.drive.uploader.network.models.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class PickedFile(
    val path: String,
    val name: String,
    val size: Long
)

class MainViewModel : ViewModel(), KoinComponent {

    private val uploadApi: UploadApi by inject()

    val uploadingInfos by lazy {
        uploader.getAllUploadInfos()
    }

    private val uploader: IUploader = IUploader.init(
        startUpload = { size ->
            val response = uploadApi.startUpload(size)
            StartUploadResponse(
                uploadId = response.uploadId,
                key = response.key,
                chunkSize = response.chunkSize,
                links = response.signedUrls
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
                parts = it.parts.map { Part(it.eTag, it.partNumber) }
            )
            uploadApi.completeUpload(request)
        },
        fileReaderContext = Any()
    )
    private val _selectedFiles = MutableStateFlow<List<PickedFile>>(emptyList())
    val selectedFiles: StateFlow<List<PickedFile>> = _selectedFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addFiles(files: List<PickedFile>) {
        files.map {
            UploadRequest(
                fileName = it.name.fileName,
                filePath = it.path.filePath,
                fileSize = it.size.fileSize,
                folderId = null,
                cloudPath = "/".cloudPath

            )
        }.also {
            uploader.upload(it)
        }
        viewModelScope.launch {
            _selectedFiles.value = _selectedFiles.value + files
        }
    }

    fun removeFile(file: PickedFile) {
        viewModelScope.launch {
            _selectedFiles.value = _selectedFiles.value - file
        }
    }

    fun clearFiles() {
        viewModelScope.launch {
            _selectedFiles.value = emptyList()
        }
    }

    fun pauseUpload(id: Long) {
        viewModelScope.launch {
            uploader.pause(id)
        }
    }

    fun resumeUpload(id: Long) {
        viewModelScope.launch {
            uploader.resume(id)
        }
    }

    fun cancelUpload(id: Long) {
        viewModelScope.launch {
            uploader.cancel(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            uploader.deleteAll()
        }
    }
}

