package ir.sharif.drive.uploader.upload

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import ir.sharif.drive.uploader.api.UploadEngineRuntime

internal object UploadNotificationFactory {
    const val CHANNEL_ID = "uploader_foreground"
    const val CHANNEL_NAME = "File uploads"
    const val FOREGROUND_NOTIFICATION_ID = 71_001
    const val COMPLETION_NOTIFICATION_ID = 71_002

    fun build(context: Context, content: UploadNotificationContent): Notification {
        val openApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.let { launchIntent ->
            PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
        val settings = UploadEngineRuntime.notificationSettings
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(content.title)
            .setContentText(content.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.text))
            .setSmallIcon(UploadNotificationResources.smallIcon(context))
            .setOngoing(content.ongoing)
            .setOnlyAlertOnce(true)
            .setAutoCancel(content.autoCancel)
            .setSilent(!settings.enabled)
            .setPriority(
                if (settings.enabled && settings.showProgressDetails) {
                    NotificationCompat.PRIORITY_DEFAULT
                } else {
                    NotificationCompat.PRIORITY_LOW
                },
            )
        if (openApp != null) {
            builder.setContentIntent(openApp)
        }
        val progress = content.progress
        when {
            progress != null && progress.second > 0 -> {
                builder.setProgress(progress.second, progress.first, false)
            }
            content.indeterminateProgress -> {
                builder.setProgress(0, 0, true)
            }
        }
        return builder.build()
    }
}
