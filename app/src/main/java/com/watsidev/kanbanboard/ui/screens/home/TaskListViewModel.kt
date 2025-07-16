package com.watsidev.kanbanboard.ui.screens.home

import androidx.lifecycle.ViewModel
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

class TaskListViewModel(
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    fun fetchTasks(columnId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        apiService.getTasksByColumn(columnId).enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(
                call: Call<List<TaskResponse>>,
                response: Response<List<TaskResponse>>
            ) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isLoading = false, tasks = response.body() ?: emptyList())
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Fallo de red: ${t.message}")
                }
            }
        })
    }
}
