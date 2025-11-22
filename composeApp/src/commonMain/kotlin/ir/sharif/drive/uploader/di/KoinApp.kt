package ir.sharif.drive.uploader.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication

@Composable
fun KoinApplication(content: @Composable () -> Unit) {
    KoinApplication(application = {
        modules(appModule)
    }) {
        content()
    }
}

