package com.watsidev.kanbanboard.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.MessageResponse
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.UpdatePasswordRequest
import com.watsidev.kanbanboard.model.network.UpdateUserRequest
import com.watsidev.kanbanboard.model.network.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Estado de la UI para la pantalla de Perfil.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val profile: User? = null, // El perfil del usuario cargado
    val updateSuccess: Boolean = false, // Flag para éxito de actualización
    val passwordSuccess: Boolean = false, // Flag para éxito de cambio de contraseña
    val deleteSuccess: Boolean = false, // Flag para éxito de eliminación

    // Campos del formulario para editar
    val formName: String = "",
    val formEmail: String = "",
    val formRole: String = "",
    val formPassword: String = "" // Para el cambio de contraseña
)

/**
 * ViewModel para gestionar el perfil de usuario.
 *
 * Asume que tu RetrofitInstance tiene una propiedad `userApi`
 * val userApi: UserApiService = Retrofit.create(UserApiService::class.java)
 */
class ProfileViewModel(
    // Asumimos que tu RetrofitInstance puede proveer el UserApiService
    // (Si usas Hilt, esto sería inyectado)
    private val userApi: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // --- Handlers para los campos del formulario ---

    fun onNameChange(name: String) {
        _uiState.update { it.copy(formName = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(formEmail = email) }
    }

    fun onRoleChange(role: String) {
        _uiState.update { it.copy(formRole = role) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(formPassword = password) }
    }

    // --- Llamadas a la API ---

    /**
     * Carga el perfil.
     * @param token El token JWT (sin "Bearer ").
     * @param userId Si es null, carga el perfil propio (/auth/me).
     * Si se provee, carga el perfil de ese usuario.
     */
    fun loadProfile(token: String, userId: Int? = null) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val authToken = "Bearer $token"

        val call = if (userId == null) {
            userApi.getMyProfile(authToken)
        } else {
            userApi.getUserProfile(authToken, userId)
        }

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = user,
                            // Rellenamos los campos del formulario con los datos cargados
                            formName = user.name,
                            formEmail = user.email,
                            formRole = user.role
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cargar perfil: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}")
                }
            }
        })
    }

    /**
     * Actualiza el perfil del usuario (nombre, email, rol).
     */
    fun updateProfile(token: String, userId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, updateSuccess = false) }
        val state = _uiState.value

        val request = UpdateUserRequest(
            name = state.formName,
            email = state.formEmail,
            role = state.formRole
        )

        userApi.updateUser("Bearer $token", userId, request).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            updateSuccess = true,
                            profile = response.body() // Actualiza el perfil con los nuevos datos
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al actualizar: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}")
                }
            }
        })
    }

    /**
     * Cambia la contraseña del usuario.
     */
    fun changePassword(token: String, userId: Int) {
        val state = _uiState.value
        if (state.formPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "La contraseña no puede estar vacía") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, passwordSuccess = false) }

        val request = UpdatePasswordRequest(password = state.formPassword)

        userApi.updatePassword("Bearer $token", userId, request).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            passwordSuccess = true,
                            formPassword = "" // Limpia el campo de contraseña
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cambiar contraseña: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}")
                }
            }
        })
    }

    /**
     * Elimina la cuenta de un usuario.
     */
    fun deleteAccount(token: String, userId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, deleteSuccess = false) }

        userApi.deleteUser("Bearer $token", userId).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isLoading = false, deleteSuccess = true, profile = null)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al eliminar cuenta: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}")
                }
            }
        })
    }

    /**
     * Limpia los flags de éxito (útil después de navegar o cerrar un modal).
     */
    fun clearSuccessFlags() {
        _uiState.update {
            it.copy(
                updateSuccess = false,
                passwordSuccess = false,
                deleteSuccess = false,
                errorMessage = null
            )
        }
    }
}
