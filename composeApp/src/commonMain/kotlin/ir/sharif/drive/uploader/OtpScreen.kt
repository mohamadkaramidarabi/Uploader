package ir.sharif.drive.uploader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    phone: String,
    viewModel: LoginViewModel,
    onBackClick: () -> Unit = {}
) {
    var codeText by remember { mutableStateOf("") }
    var remainingTime by remember { mutableStateOf(60) }
    var showTimer by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(showTimer) {
        if (showTimer) {
            remainingTime = 60
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            showTimer = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        TopAppBar(
            title = { Text("Verify Code") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Text("<")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Enter the verification code sent to $phone",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = codeText,
                onValueChange = {
                    val filtered = it.filter { c -> c.isDigit() }.take(6)
                    codeText = filtered
                    if (filtered.length == 6) {
                        viewModel.verifyCode(
                            phone,
                            filtered.toIntOrNull()
                        )
                    }
                },
                label = { Text("Code") },
                placeholder = { Text("123456") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.verifyCode(
                        phone,
                        codeText.toIntOrNull()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = codeText.length == 6
            ) {
                Text("Verify")
            }

            Spacer(Modifier.height(16.dp))

            if (showTimer) {
                val minutes = remainingTime / 60
                val seconds = remainingTime % 60
                val timeString = "$minutes:${seconds.toString().padStart(2, '0')}"
                TextButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resend code ($timeString)")
                }
            } else {
                TextButton(
                    onClick = {
                        codeText = ""
                        showTimer = true
                        viewModel.resendCode(
                            phone
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resend code")
                }
            }
        }
    }
}

