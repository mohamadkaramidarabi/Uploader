package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.models.UploadNotificationSettings
import ir.sharif.drive.uploader.upload.UploadForegroundService
import ir.sharif.drive.uploader.upload.UploadNotificationPermissionHost

actual fun platformUploadNotificationSettings(): UploadNotificationSettings =
    UploadNotificationSettings(enabled = true, showProgressDetails = true)

internal actual fun onActiveUploadsDetected() {
    val ctx = UploadAndroidRuntime.appContext ?: return
    UploadNotificationPermissionHost.requestIfNeeded()
    UploadForegroundService.enqueue(ctx)
}

actual fun ensureUploadNotificationsEnabled() {
    UploadNotificationPermissionHost.requestIfNeeded()
}
