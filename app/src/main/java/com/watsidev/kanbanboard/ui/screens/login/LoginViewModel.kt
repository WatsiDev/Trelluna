package com.watsidev.kanbanboard.ui.screens.login

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.login.LoginRequest
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.AuthResponse
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.User
import com.watsidev.kanbanboard.model.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Estado de la UI para Login
 * ¡Ahora incluye el token!
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
    val loginSuccess: Boolean = false,
    val user: User? = null,
    val token: String? = null // <-- ¡AQUÍ ESTÁ EL TOKEN!
)

class LoginViewModel(
    // Asumimos que RetrofitInstance.userApi existe, tal como en ProfileViewModel
    private val userApi: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun loginUser() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isEmailError = true,
                    errorMessage = "Email requerido"
                )
            }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isPasswordError = true,
                    errorMessage = "Contraseña requerida"
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Usamos el LoginRequest del paquete ...model.data.users
        val request = LoginRequest(email = state.email, password = state.password)

        // Usamos la nueva userApi y esperamos AuthResponse
        userApi.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token

                    // 2. ¡Éxito en Login! AHORA inicializamos la sesión (llamada a /me)
                    SessionManager.initializeSession(
                        token = token,
                        onSessionReady = {
                            // 3. ¡Éxito en /me! Sesión lista.
                            // Ahora sí, marcamos loginSuccess = true
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loginSuccess = true
                                )
                            }
                        },
                        onError = { errorMsg ->
                            // 3b. Falló la llamada a /me
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoginError = true,
                                    errorMessage = "Login exitoso, pero falló al obtener perfil: $errorMsg"
                                )
                            }
                        }
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginError = true,
                            errorMessage = "Usuario o contraseña incorrectos",
                        )
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de red: ${t.message}"
                    )
                }
            }
        })
    }

    fun clearUiState() {
        _uiState.value = LoginUiState()
    }
}
