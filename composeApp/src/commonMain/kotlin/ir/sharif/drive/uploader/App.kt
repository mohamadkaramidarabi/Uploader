package ir.sharif.drive.uploader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.di.KoinApplication
import ir.sharif.drive.uploader.main.MainScreen
import ir.sharif.drive.uploader.models.StartUploadResponse
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

sealed class Screen {
    data object Phone : Screen()
    data class Otp(val phone: String) : Screen()
    data object Main : Screen()
}

@Composable
@Preview
fun App() {
    KoinApplication {
        MaterialTheme {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Phone) }
            var phone by remember { mutableStateOf("") }
            val viewModel: LoginViewModel = koinViewModel()
            LaunchedEffect(Unit) {
                viewModel.codeSent.collect { codeSent ->
                    if (codeSent) {
                        currentScreen = Screen.Otp(phone = phone)
                    }
                }
            }
            LaunchedEffect(Unit) {
                viewModel.loginSuccess.collect {
                    if (it) {
                        currentScreen = Screen.Main
                    }
                }
            }


            when (val screen = currentScreen) {
                is Screen.Phone -> {
                    PhoneScreen(
                        viewModel = viewModel,
                        onNavigateToOtp = {
                            phone = it
                        },
                        onBackClick = {}
                    )
                }
                is Screen.Otp -> {
                    OtpScreen(
                        phone = screen.phone,
                        viewModel = viewModel,
                        onBackClick = {
                            currentScreen = Screen.Phone
                        }
                    )
                }
                is Screen.Main -> {
                    MainScreen()
                }
            }
        }
    }
}

