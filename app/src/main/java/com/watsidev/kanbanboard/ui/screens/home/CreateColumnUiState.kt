package com.watsidev.kanbanboard.ui.screens.home

import com.watsidev.kanbanboard.model.data.columns.Column

data class ColumnUiState(
    // Datos
    val columns: List<Column> = emptyList(),
    val selectedColumn: Column? = null,

    // Estado del formulario (usado para crear y actualizar)
    val formName: String = "",
    val formProjectId: String = "", // Solo necesario para crear

    // Estado de la UI
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val operationSuccess: Boolean = false // Se activa en true tras un CUD exitoso
)