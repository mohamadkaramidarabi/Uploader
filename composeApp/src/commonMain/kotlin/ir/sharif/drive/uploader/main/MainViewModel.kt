package ir.sharif.drive.uploader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.api.ensureUploadNotificationsEnabled
import ir.sharif.drive.uploader.models.CloudPath.Companion.cloudPath
import ir.sharif.drive.uploader.models.FileName.Companion.fileName
import ir.sharif.drive.uploader.models.FilePath.Companion.filePath
import ir.sharif.drive.uploader.models.FileSize.Companion.fileSize
import ir.sharif.drive.uploader.models.UploadInfo
import ir.sharif.drive.uploader.models.UploadRequest
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

    private val uploader: IUploader by inject()

    val uploadingInfos by lazy {
        uploader.getAllUploadInfos()
    }

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
                cloudPath = "/".cloudPath,
                versionGroup = null,
                metaData = null,
            )
        }.also {
            ensureUploadNotificationsEnabled()
            uploader.upload(it)
        }
        viewModelScope.launch {
            _selectedFiles.value += files
        }
    }

    fun removeFile(file: PickedFile) {
        viewModelScope.launch {
            _selectedFiles.value -= file
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

    fun retry(uploadInfo: UploadInfo) {
        viewModelScope.launch {
            uploader.retry(uploadInfo.id)
        }
    }
}
