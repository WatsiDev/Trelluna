package com.watsidev.kanbanboard.ui.screens.login

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.login.LoginRequest
import com.watsidev.kanbanboard.model.data.login.LoginResponse
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun loginUser() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Email y contraseña requeridos")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        val request = LoginRequest(email = state.email, password = state.password)

        RetrofitInstance.api.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        user = response.body()?.user
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales inválidas"
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }
    fun clearUiState() {
        _uiState.value = LoginUiState()
    }
}