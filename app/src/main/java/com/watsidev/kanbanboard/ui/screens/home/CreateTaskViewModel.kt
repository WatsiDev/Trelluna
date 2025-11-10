package com.watsidev.kanbanboard.ui.screens.home

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.tasks.NewTaskRequest
import com.watsidev.kanbanboard.model.data.tasks.TaskResponse
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTaskViewModel(
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    // Usamos el nuevo UiState
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    fun onPriorityChange(newPriority: String) {
        _uiState.update { it.copy(priority = newPriority) }
    }

    // --- onColumnIdChange y onCreatedByChange eliminados ---

    fun createTask(id: Int) { // 'id' es el column_id que viene de la UI
        val state = _uiState.value

        // 1. Obtenemos el ID del usuario desde nuestra sesión
        val userId = SessionManager.getUserId()

        // 2. Validamos los campos del formulario
        if (state.title.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Título y descripción son requeridos") }
            return
        }

        // 3. Validamos que tengamos un usuario
        if (userId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: No se pudo identificar al usuario. Inicia sesión de nuevo.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

        // 4. Creamos el request con el 'id' (column_id) y el 'userId' (created_by)
        val request = NewTaskRequest(
            title = state.title,
            description = state.description,
            priority = state.priority, // "Alto", "Medio" o "Bajo"
            column_id = id.toString(),  // El ID de la columna que recibimos
            created_by = userId         // El ID del usuario de la sesión
        )

        apiService.createTask(request).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.code()}") }
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}") }
            }
        })
    }
}