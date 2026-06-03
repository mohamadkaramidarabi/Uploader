package ir.sharif.drive.uploader.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal object UploadBackgroundObserver {
    private var observeJob: Job? = null

    fun start(uploader: IUploader, scope: CoroutineScope) {
        UploadEngineHolder.register(uploader)
        observeJob?.cancel()
        observeJob = scope.launch {
            uploader.getAllUploadInfos()
                .map { uploads -> uploads.any { it.state.isEngineActive() } }
                .distinctUntilChanged()
                .catch { emit(false) }
                .collect { active ->
                    if (active) {
                        onActiveUploadsDetected()
                    }
                }
        }
    }
}
