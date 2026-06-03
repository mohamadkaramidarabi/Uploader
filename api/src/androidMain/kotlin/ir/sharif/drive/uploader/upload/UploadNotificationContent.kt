package ir.sharif.drive.uploader.upload

internal data class UploadNotificationContent(
    val title: String,
    val text: String,
    val ongoing: Boolean,
    val progress: Pair<Int, Int>?,
    val autoCancel: Boolean = false,
    val indeterminateProgress: Boolean = false,
)

internal sealed interface UploadForegroundState {
    data object Idle : UploadForegroundState

    data class Foreground(val content: UploadNotificationContent) : UploadForegroundState

    data class CompletionVisible(val content: UploadNotificationContent) : UploadForegroundState
}
