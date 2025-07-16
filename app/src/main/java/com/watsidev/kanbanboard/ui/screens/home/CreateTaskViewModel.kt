package com.watsidev.kanbanboard.ui.screens.home

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.tasks.NewTaskRequest
import com.watsidev.kanbanboard.model.data.tasks.TaskResponse
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
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

    fun onColumnIdChange(newColumnId: String) {
        _uiState.update { it.copy(columnId = newColumnId) }
    }

    fun onCreatedByChange(newCreatedBy: String) {
        _uiState.update { it.copy(createdBy = newCreatedBy) }
    }

    fun createTask(id :Int) {
        val state = _uiState.value
        if (state.title.isBlank() || state.description.isBlank() || state.priority.isBlank() || state.createdBy.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

        val request = NewTaskRequest(
            title = state.title,
            description = state.description,
            priority = state.priority,
            column_id = id.toString(),
            created_by = state.createdBy
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