package com.watsidev.kanbanboard.ui.screens.home

import com.watsidev.kanbanboard.model.data.columns.ColumnResponse

data class ColumnListUiState(
    val isLoading: Boolean = false,
    val columns: List<ColumnResponse> = emptyList(),
    val errorMessage: String? = null
)
