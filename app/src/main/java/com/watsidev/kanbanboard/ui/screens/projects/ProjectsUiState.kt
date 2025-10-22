package com.watsidev.kanbanboard.ui.screens.projects

import com.watsidev.kanbanboard.model.network.Project

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
)