package ir.sharif.drive.uploader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ir.sharif.drive.uploader.upload.UploadNotificationPermissionHost
import ir.sharif.drive.uploader.upload.UploadNotificationPermissions

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PlatformProvider.init(this)
        UploadNotificationPermissionHost.activity = this
        UploadNotificationPermissionHost.launcher = notificationPermissionLauncher
        UploadNotificationPermissions.requestIfNeeded(this, notificationPermissionLauncher)

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        if (UploadNotificationPermissionHost.activity === this) {
            UploadNotificationPermissionHost.activity = null
            UploadNotificationPermissionHost.launcher = null
        }
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
