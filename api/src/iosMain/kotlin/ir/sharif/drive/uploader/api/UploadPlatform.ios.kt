package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.models.UploadNotificationSettings

actual fun platformUploadNotificationSettings(): UploadNotificationSettings =
    UploadNotificationSettings(enabled = false)

internal actual fun onActiveUploadsDetected() = Unit

actual fun ensureUploadNotificationsEnabled() = Unit
