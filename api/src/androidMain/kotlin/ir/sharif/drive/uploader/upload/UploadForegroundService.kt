package ir.sharif.drive.uploader.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import ir.sharif.drive.uploader.api.UploadEngineRuntime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch

class UploadForegroundService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main.immediate)

    private val viewModel: UploadForegroundViewModel
        get() = UploadForegroundViewModelOwner.viewModel()

    private var stateJob: Job? = null
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        createChannelIfNeeded()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stateJob?.cancel()

        val preparing = viewModel.preparingNotificationContent()
            ?: fallbackPreparingContent()
        promoteToForeground(preparing)

        if (!viewModel.startMonitoring()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        stateJob = serviceScope.launch {
            viewModel.state
                .dropWhile { it is UploadForegroundState.Idle }
                .collect { state -> renderState(state) }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stateJob?.cancel()
        viewModel.stopMonitoring()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun promoteToForeground(content: UploadNotificationContent) {
        val notification = UploadNotificationFactory.build(this, content)
        ServiceCompat.startForeground(
            this,
            UploadNotificationFactory.FOREGROUND_NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    private fun renderState(state: UploadForegroundState) {
        when (state) {
            UploadForegroundState.Idle -> {
                notificationManager.cancel(UploadNotificationFactory.FOREGROUND_NOTIFICATION_ID)
                notificationManager.cancel(UploadNotificationFactory.COMPLETION_NOTIFICATION_ID)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            is UploadForegroundState.Foreground -> promoteToForeground(state.content)
            is UploadForegroundState.CompletionVisible -> {
                notificationManager.cancel(UploadNotificationFactory.FOREGROUND_NOTIFICATION_ID)
                stopForeground(STOP_FOREGROUND_REMOVE)
                if (UploadNotificationPermissions.canPostNotifications(this)) {
                    notificationManager.notify(
                        UploadNotificationFactory.COMPLETION_NOTIFICATION_ID,
                        UploadNotificationFactory.build(this, state.content),
                    )
                }
            }
        }
    }

    private fun fallbackPreparingContent() = UploadNotificationContent(
        title = "Uploading",
        text = "Preparing uploads…",
        ongoing = true,
        progress = null,
        indeterminateProgress = true,
    )

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val settings = UploadEngineRuntime.notificationSettings
        val importance = when {
            !settings.enabled -> NotificationManager.IMPORTANCE_LOW
            settings.showProgressDetails -> NotificationManager.IMPORTANCE_DEFAULT
            else -> NotificationManager.IMPORTANCE_LOW
        }
        val channel = NotificationChannel(
            UploadNotificationFactory.CHANNEL_ID,
            UploadNotificationFactory.CHANNEL_NAME,
            importance,
        ).apply {
            description = "Upload progress and status"
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        fun enqueue(context: Context) {
            ContextCompat.startForegroundService(
                context.applicationContext,
                Intent(context.applicationContext, UploadForegroundService::class.java),
            )
        }
    }
}
