package com.watsidev.kanbanboard.ui.screens.home

data class CreateColumnUiState(
    val name: String = "",
    val position: String = "", // texto para que sea compatible con el TextField
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
