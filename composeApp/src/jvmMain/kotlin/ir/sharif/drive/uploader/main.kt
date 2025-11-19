package ir.sharif.drive.uploader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Uploader",
    ) {
        App()
    }
}