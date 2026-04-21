package ir.sharif.drive.uploader.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun KoinApplication(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(appModule) }),
        content = {
            content()
        })
}

