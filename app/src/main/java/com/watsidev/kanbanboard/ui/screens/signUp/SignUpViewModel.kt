package com.watsidev.kanbanboard.ui.screens.signUp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.register.NewUserRequest
import com.watsidev.kanbanboard.model.data.register.UserCreatedResponse
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.AuthResponse
import com.watsidev.kanbanboard.model.network.RegisterRequest
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Estado de la UI actualizado para el registro
 */
data class SignUpUiState(
    val name: String = "", // Cambiado de 'username' a 'name' para coincidir con la API
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val registrationCompleted: Boolean = false,
    val user: User? = null, // <-- Dato del usuario
    val token: String? = null // <-- Token JWT
)

class SignUpViewModel(
    // Inyectamos la nueva UserApiService
    private val userApi: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    // Actualizado a onNameChange
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun clearUiState() {
        _uiState.value = SignUpUiState()
    }

    // Función para registrar usuario usando la nueva API
    fun registerUser() {
        val state = _uiState.value

        // Validar que no haya campos vacíos
        if (state.email.isBlank() || state.name.isBlank() ||
            state.password.isBlank() || state.confirmPassword.isBlank()
        ) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Por favor completa todos los campos"
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isError = false) }

        // Validar que password y confirmPassword coincidan
        if (state.password != state.confirmPassword) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Las contraseñas no coinciden"
                )
            }
            return
        }

        // Construir request para la API con el nuevo modelo
        val request = RegisterRequest(
            name = state.name,
            email = state.email,
            password = state.password
            // El rol se asigna en el backend para /register
        )

        // Usamos la nueva API y esperamos AuthResponse
        userApi.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationCompleted = true,
                            user = authData.user, // Guardamos el usuario
                            token = authData.token // Guardamos el token
                        )
                    }
                    Log.d("SignUpViewModel", "Usuario creado exitosamente: ${authData.token}")
                } else {
                    // Manejo de error (ej. 409 - Email ya existe)
                    val errorMsg = if (response.code() == 409) {
                        "El email ya está registrado"
                    } else {
                        response.errorBody()?.string() ?: "Error desconocido"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Error al registrar usuario: $errorMsg"
                        )
                    }
                    Log.e("SignUpViewModel", "Error al registrar usuario: $errorMsg")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error de red: ${t.message}"
                    )
                }
                Log.e("SignUpViewModel", "Error de red: ${t.message}")
            }
        })
    }
}
