package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.models.UploadNotificationSettings

expect fun platformUploadNotificationSettings(): UploadNotificationSettings

internal expect fun onActiveUploadsDetected()
