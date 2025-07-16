package com.watsidev.kanbanboard.ui.screens.home

import com.watsidev.kanbanboard.model.data.tasks.TaskResponse

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskResponse> = emptyList(),
    val errorMessage: String? = null
)
