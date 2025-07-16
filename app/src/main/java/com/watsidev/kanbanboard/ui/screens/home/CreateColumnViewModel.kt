package com.watsidev.kanbanboard.ui.screens.home

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.model.data.columns.NewColumnRequest
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateColumnViewModel(
    private val apiService: ApiService = RetrofitInstance.api // ajusta si usas Hilt
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateColumnUiState())
    val uiState: StateFlow<CreateColumnUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onPositionChange(newPosition: String) {
        _uiState.update { it.copy(position = newPosition) }
    }

    fun createColumn() {
        val name = _uiState.value.name
        val positionInt = _uiState.value.position.toIntOrNull()

        if (name.isBlank() || positionInt == null) {
            _uiState.update {
                it.copy(errorMessage = "Completa todos los campos correctamente")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val request = NewColumnRequest(name, positionInt)
        apiService.createColumn(request).enqueue(object : Callback<ColumnResponse> {
            override fun onResponse(call: Call<ColumnResponse>, response: Response<ColumnResponse>) {
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: ${response.code()}"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ColumnResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Fallo de red: ${t.message}"
                    )
                }
            }
        })
    }
}
