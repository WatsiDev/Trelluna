package com.watsidev.kanbanboard.ui.screens.home

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: String = "",
    val columnId: String = "",
    val createdBy: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)