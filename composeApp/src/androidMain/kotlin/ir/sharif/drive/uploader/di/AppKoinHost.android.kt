package ir.sharif.drive.uploader.di

import androidx.compose.runtime.Composable

@Composable
actual fun AppKoinHost(content: @Composable () -> Unit) {
    content()
}
