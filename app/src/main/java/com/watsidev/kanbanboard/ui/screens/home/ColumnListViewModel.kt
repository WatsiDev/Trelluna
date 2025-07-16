package com.watsidev.kanbanboard.ui.screens.home

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColumnListViewModel(
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColumnListUiState())
    val uiState: StateFlow<ColumnListUiState> = _uiState.asStateFlow()

    init {
        fetchColumns()
    }

    fun fetchColumns() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        apiService.getColumns().enqueue(object : Callback<List<ColumnResponse>> {
            override fun onResponse(
                call: Call<List<ColumnResponse>>,
                response: Response<List<ColumnResponse>>
            ) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        columns = response.body() ?: emptyList()
                    )
                }
            }

            override fun onFailure(call: Call<List<ColumnResponse>>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${t.message}"
                    )
                }
            }
        })
    }
}
