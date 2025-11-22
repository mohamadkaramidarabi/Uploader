package ir.sharif.drive.uploader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : ViewModel(), KoinComponent {
    private val authApi: AuthApi by inject()
    private val tokenStorage: TokenStorage by inject()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _codeSent = MutableStateFlow(false)
    val codeSent: StateFlow<Boolean> = _codeSent.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStorage.getTokens()?.let {
                _loginSuccess.value = true
            }
        }
    }

    fun sendCode(phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                if (phone.isBlank() || !phone.matches(Regex("^09\\d{9}$"))) {
                    _error.value = "Invalid phone number"
                    _error.emit("Invalid phone number")
                    return@launch
                }
                val response = authApi.generateCode(phone)
                _codeSent.value = true
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _error.value = errorMessage
                _error.emit(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyCode(phone: String, code: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                if (code == null || code.toString().length != 6) {
                    _error.emit("Invalid code")
                    return@launch
                }

                val response = authApi.login(phone, code)
                
                if (response.access != null) {
                    val tokensToSave = response.copy(
                        access = if (!response.access.startsWith("Bearer ")) "Bearer ${response.access}" else response.access
                    )
                    tokenStorage.saveTokens(tokensToSave)
                    _loginSuccess.value = true
                } else {
                    val errorMessage = "Login failed"
                    _error.value = errorMessage
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _error.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendCode(phone: String) {
        sendCode(phone)
    }
}

