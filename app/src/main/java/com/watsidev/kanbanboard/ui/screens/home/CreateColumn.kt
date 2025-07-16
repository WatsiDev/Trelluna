package com.watsidev.kanbanboard.ui.screens.home

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@Composable
fun CreateColumnScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateColumnViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Add new column"
        )
        InputKanban(
            text = state.name,
            onTextChange = { viewModel.onNameChange(it) },
            label = "Name",
            leadingIcon = Icons.Outlined.Dashboard,
            modifier = Modifier.fillMaxWidth()
        )
        InputKanban(
            text = state.position,
            onTextChange = { viewModel.onPositionChange(it) },
            label = "Position",
            leadingIcon = Icons.Outlined.FormatListNumbered,
            modifier = Modifier.fillMaxWidth()
        )
        // Mensaje de error
        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        ButtonKanban(
            text = if (state.isLoading) "Creating..." else "Create",
            icon = Icons.Outlined.Create,
            onClick = { viewModel.createColumn() },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.isSuccess){
            Text(
                "Column created successfully!",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
