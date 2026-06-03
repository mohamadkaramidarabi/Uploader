package ir.sharif.drive.uploader.api

import android.app.Application

fun initializeAndroidUploadPlatform(application: Application) {
    UploadAndroidRuntime.appContext = application.applicationContext
}
