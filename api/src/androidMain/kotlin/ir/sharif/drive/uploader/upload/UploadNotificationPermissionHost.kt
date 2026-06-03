package ir.sharif.drive.uploader.upload

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher

object UploadNotificationPermissionHost {
    var activity: ComponentActivity? = null
    var launcher: ActivityResultLauncher<String>? = null

    fun requestIfNeeded() {
        val act = activity ?: return
        val launch = launcher ?: return
        UploadNotificationPermissions.requestIfNeeded(act, launch)
    }
}
