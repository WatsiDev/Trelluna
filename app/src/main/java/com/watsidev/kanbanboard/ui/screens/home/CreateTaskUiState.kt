package com.watsidev.kanbanboard.ui.screens.home

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    // Valor por defecto para el dropdown, coincide con una de las opciones
    val priority: String = "Medio",

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)