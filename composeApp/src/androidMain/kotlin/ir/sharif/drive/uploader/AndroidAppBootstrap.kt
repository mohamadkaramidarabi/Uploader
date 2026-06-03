package ir.sharif.drive.uploader

import android.app.Application
import ir.sharif.drive.uploader.api.initializeAndroidUploadPlatform
import ir.sharif.drive.uploader.database.initializeUploadDatabasePlatform

fun initializeAndroidUploaderPlatform(application: Application) {
    initializeUploadDatabasePlatform(application)
    initializeAndroidUploadPlatform(application)
}
