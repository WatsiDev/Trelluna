package com.watsidev.kanbanboard.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@Composable
fun CreateTaskScreen(
    id: Int,
    modifier: Modifier = Modifier,
    viewModel: CreateTaskViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Columna: $id")
        InputKanban(
            text = state.title,
            onTextChange = viewModel::onTitleChange,
            label = "Title",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
        InputKanban(
            text = state.description,
            onTextChange = viewModel::onDescriptionChange,
            label = "Description",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
        InputKanban(
            text = state.priority,
            onTextChange = viewModel::onPriorityChange,
            label = "Priority (e.g. important)",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
        InputKanban(
            text = state.createdBy,
            onTextChange = viewModel::onCreatedByChange,
            label = "Created By",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )

        ButtonKanban(
            text = if (state.isLoading) "Creating..." else "Create Task",
            icon = Icons.Outlined.Task,
            onClick = { viewModel.createTask(id) },
            modifier = Modifier.fillMaxWidth()
        )

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (state.isSuccess) {
            Text("Task created successfully!", color = MaterialTheme.colorScheme.primary)
        }
    }
}
