@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package ir.sharif.drive.uploader.upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.drive.uploader.api.R
import ir.sharif.drive.uploader.api.UploadAndroidRuntime
import ir.sharif.drive.uploader.api.UploadEngineHolder
import ir.sharif.drive.uploader.api.allUploadsSettled
import ir.sharif.drive.uploader.api.isEngineActive
import ir.sharif.drive.uploader.api.uploadProgress
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class UploadForegroundViewModel : ViewModel() {

    private val _state = MutableStateFlow<UploadForegroundState>(UploadForegroundState.Idle)
    val state: StateFlow<UploadForegroundState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var seenActive = false
    private var sessionFinished = false

    fun preparingNotificationContent(): UploadNotificationContent? {
        val context = appContext() ?: return null
        return preparingContent(context)
    }

    /** @return false when the engine is not ready; caller must still have called [startForeground] first. */
    fun startMonitoring(): Boolean {
        val uploader = UploadEngineHolder.uploader ?: return false
        val context = appContext() ?: return false

        observeJob?.cancel()
        seenActive = false
        sessionFinished = false
        _state.value = UploadForegroundState.Foreground(preparingContent(context))

        observeJob = viewModelScope.launch {
            uploader.getAllUploadInfos()
                .debounce { uploads ->
                    val anyActive = uploads.any { it.state.isEngineActive() }
                    if (anyActive) 0L else 600L
                }
                .catch { emit(emptyList()) }
                .collect { uploads ->
                    processUploads(context, uploads)
                }
        }
        return true
    }

    fun stopMonitoring() {
        observeJob?.cancel()
        observeJob = null
        _state.value = UploadForegroundState.Idle
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }

    private suspend fun processUploads(context: Context, uploads: List<UploadInfo>) {
        if (sessionFinished) return

        val activeUploads = uploads.filter { it.state.isEngineActive() }
        when {
            activeUploads.isNotEmpty() -> {
                seenActive = true
                _state.value = UploadForegroundState.Foreground(
                    progressContent(context, activeUploads),
                )
            }
            uploads.isEmpty() -> finishSession(UploadForegroundState.Idle)
            seenActive || uploads.allUploadsSettled() -> finishSession(
                completionState(context, uploads),
            )
            else -> {
                _state.value = UploadForegroundState.Foreground(startedContent(context))
            }
        }
    }

    private suspend fun finishSession(terminalState: UploadForegroundState) {
        sessionFinished = true
        observeJob?.cancel()
        observeJob = null
        when (terminalState) {
            is UploadForegroundState.CompletionVisible -> {
                _state.value = terminalState
                delay(COMPLETION_VISIBLE_MS.milliseconds)
                _state.value = UploadForegroundState.Idle
            }
            else -> _state.value = terminalState
        }
    }

    private fun completionState(context: Context, uploads: List<UploadInfo>): UploadForegroundState {
        if (!seenActive) return UploadForegroundState.Idle
        val succeeded = uploads.count { it.state == States.UploadInfo.State.SUCCESS }
        val failed = uploads.count { it.state == States.UploadInfo.State.FAILED }
        return UploadForegroundState.CompletionVisible(
            completionContent(context, succeeded, failed),
        )
    }

    private fun preparingContent(context: Context) = UploadNotificationContent(
        title = context.getString(R.string.upload_notification_title),
        text = context.getString(R.string.upload_notification_preparing),
        ongoing = true,
        progress = null,
        indeterminateProgress = true,
    )

    private fun startedContent(context: Context) = UploadNotificationContent(
        title = context.getString(R.string.upload_notification_title),
        text = context.getString(R.string.upload_notification_started),
        ongoing = true,
        progress = null,
        indeterminateProgress = true,
    )

    private fun progressContent(context: Context, activeUploads: List<UploadInfo>): UploadNotificationContent {
        val primary = activeUploads.first()
        val progress = primary.uploadProgress()
        val text = when {
            activeUploads.size > 1 -> context.getString(R.string.upload_notification_multiple)
            progress != null -> context.getString(
                R.string.upload_notification_progress,
                primary.name.value,
                progress.first * 100 / progress.second.coerceAtLeast(1),
            )
            else -> primary.name.value
        }
        return UploadNotificationContent(
            title = context.getString(R.string.upload_notification_title),
            text = text,
            ongoing = true,
            progress = progress,
        )
    }

    private fun completionContent(context: Context, succeeded: Int, failed: Int): UploadNotificationContent {
        val text = when {
            failed > 0 -> context.getString(
                R.string.upload_notification_complete_with_failures,
                succeeded,
                failed,
            )
            succeeded > 0 -> context.getString(
                R.string.upload_notification_complete,
                succeeded,
            )
            else -> context.getString(R.string.upload_notification_failed)
        }
        return UploadNotificationContent(
            title = context.getString(R.string.upload_notification_title),
            text = text,
            ongoing = false,
            progress = null,
            autoCancel = true,
        )
    }

    private fun appContext(): Context? = UploadAndroidRuntime.appContext

    companion object {
        private const val COMPLETION_VISIBLE_MS = 5_000L
    }
}
