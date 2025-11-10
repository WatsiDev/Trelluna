package com.watsidev.kanbanboard.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.columns.Column
import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.model.data.columns.CreateColumnRequest
import com.watsidev.kanbanboard.model.data.columns.MessageResponse
import com.watsidev.kanbanboard.model.data.columns.NewColumnRequest
import com.watsidev.kanbanboard.model.data.columns.UpdateColumnRequest
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.RetrofitInstance.api
import com.watsidev.kanbanboard.ui.screens.projects.ProjectEvent
import com.watsidev.kanbanboard.ui.screens.projects.ProjectUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColumnViewModel(
    // Inyecta el nuevo servicio.
    // Asegúrate de que tu RetrofitInstance provea una instancia de ColumnApiService
    private val apiService: ApiService = RetrofitInstance.api // ¡Ajusta esto!
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColumnUiState())
    val uiState: StateFlow<ColumnUiState> = _uiState.asStateFlow()

    // --- Funciones para actualizar el estado del formulario ---

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(formName = newName) }
    }

    fun onProjectIdChange(newProjectId: String) {
        _uiState.update { it.copy(formProjectId = newProjectId) }
    }

    /**
     * Carga el estado de un Column existente en el formulario (para editar).
     */
    fun loadColumnForEdit(column: Column) {
        _uiState.update {
            it.copy(
                formName = column.name,
                formProjectId = column.projectId.toString() // No es necesario para update, pero útil
            )
        }
    }

    // --- Funciones de API (CRUD) ---

    /**
     * GET /api/columns
     * Obtiene columnas, opcionalmente filtradas por ID de proyecto.
     */
    fun getColumns(projectId: Int?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        apiService.getColumns(projectId).enqueue(object : Callback<List<Column>> {
            override fun onResponse(call: Call<List<Column>>, response: Response<List<Column>>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            columns = response.body() ?: emptyList()
                        )
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<List<Column>>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    /**
     * GET /api/columns/:id
     * Obtiene una sola columna por su ID.
     */
    fun getColumnById(columnId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        apiService.getColumnById(columnId).enqueue(object : Callback<Column> {
            override fun onResponse(call: Call<Column>, response: Response<Column>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedColumn = response.body()
                        )
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Column>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    /**
     * POST /api/columns
     * Crea una nueva columna usando el estado del formulario.
     */
    fun createColumn(projectId: Int) {
        val name = _uiState.value.formName
        val projectIdInt = projectId

        if (name.isBlank() || projectIdInt == null) {
            _uiState.update { it.copy(errorMessage = "Nombre y ID de Proyecto son requeridos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val request = CreateColumnRequest(name, projectIdInt)
        apiService.createColumn(request).enqueue(object : Callback<Column> {
            override fun onResponse(call: Call<Column>, response: Response<Column>) {
                if (response.isSuccessful) {
                    val newColumn = response.body()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            // Añade la nueva columna a la lista actual para no tener que recargar
                            columns = if (newColumn != null) it.columns + newColumn else it.columns,
                            formName = "", // Limpia formulario
                            formProjectId = "" // Limpia formulario
                        )
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Column>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    /**
     * PUT /api/columns/:id
     * Actualiza una columna usando el nombre del formulario.
     */
    fun updateColumn(columnId: Int) {
        val name = _uiState.value.formName
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val request = UpdateColumnRequest(name)
        apiService.updateColumn(columnId, request).enqueue(object : Callback<Column> {
            override fun onResponse(call: Call<Column>, response: Response<Column>) {
                if (response.isSuccessful) {
                    val updatedColumn = response.body()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            // Actualiza la columna en la lista
                            columns = it.columns.map { col ->
                                if (col.id == columnId && updatedColumn != null) updatedColumn else col
                            }
                        )
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Column>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    /**
     * DELETE /api/columns/:id
     * Elimina una columna.
     */
    fun deleteColumn(columnId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        apiService.deleteColumn(columnId).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            // Elimina la columna de la lista
                            columns = it.columns.filter { col -> col.id != columnId }
                        )
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    // --- Helpers para manejar estado ---

    private fun handleApiError(code: Int) {
        _uiState.update {
            it.copy(isLoading = false, errorMessage = "Error API: $code")
        }
    }

    private fun handleNetworkError(t: Throwable) {
        _uiState.update {
            it.copy(isLoading = false, errorMessage = "Error de red: ${t.message}")
        }
    }

    /**
     * Limpia el mensaje de error una vez mostrado (ej. en un Snackbar).
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Resetea el flag de éxito (ej. después de navegar o mostrar un mensaje).
     */
    fun clearSuccessFlag() {
        _uiState.update { it.copy(operationSuccess = false) }
    }
}