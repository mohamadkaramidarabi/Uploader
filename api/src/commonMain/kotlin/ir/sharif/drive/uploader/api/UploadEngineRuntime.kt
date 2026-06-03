package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.models.UploadNotificationSettings
import kotlin.concurrent.Volatile

/**
 * Holds runtime options for platform integrations (notifications, foreground service) so the host
 * app can read the latest values without tight coupling to [IUploader] construction order.
 */
object UploadEngineRuntime {
    @Volatile
    var notificationSettings: UploadNotificationSettings = UploadNotificationSettings()
        private set

    internal fun updateFromUploader(settings: UploadNotificationSettings) {
        notificationSettings = settings
    }
}
