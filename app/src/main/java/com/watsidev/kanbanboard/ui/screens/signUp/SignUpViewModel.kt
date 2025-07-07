package com.watsidev.kanbanboard.ui.screens.signUp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.register.NewUserRequest
import com.watsidev.kanbanboard.model.data.register.UserCreatedResponse
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun clearUiState() {
        _uiState.value = SignUpUiState()
    }

    // Función para registrar usuario usando Retrofit
    fun registerUser(){
//        val currentState = _uiState.value
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        // Validar que no haya campos vacíos (puedes mejorar esta validación)
        if (_uiState.value.email.isBlank() || _uiState.value.username.isBlank() ||
            _uiState.value.password.isBlank() || _uiState.value.confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Por favor completa todos los campos"
            )
            return
        }

        // Validar que password y confirmPassword coincidan
        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Las contraseñas no coinciden"
            )
            return
        }

        // Construir request para la API (ajusta nombres según tu backend)
        val newUser = NewUserRequest(
            name = _uiState.value.username,
            email = _uiState.value.email,
            password = _uiState.value.password,
            role = "usuario" // o cualquier rol fijo que uses
        )

        RetrofitInstance.api.createUser(newUser).enqueue(object : Callback<UserCreatedResponse> {
            override fun onResponse(call: Call<UserCreatedResponse>, response: Response<UserCreatedResponse>) {
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Usuario creado exitosamente",
                        registrationCompleted = true
                    )
                    Log.d("SignUpViewModel", "Usuario creado exitosamente: ${_uiState.value.registrationCompleted} - ${response.body()}")
//                    clearUiState()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al registrar usuario: $errorMessage"
                    )
                    Log.e("SignUpViewModel", "Error al registrar usuario: $errorMessage")
                }

            }

            override fun onFailure(call: Call<UserCreatedResponse>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${t.message}"
                )
                Log.e("SignUpViewModel", "Error al registrar usuario: ${t.message}")
            }
        })
    }
}
