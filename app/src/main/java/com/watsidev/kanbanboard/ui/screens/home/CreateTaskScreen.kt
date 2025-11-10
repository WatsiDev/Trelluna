package com.watsidev.kanbanboard.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@OptIn(ExperimentalMaterial3Api::class)
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
            leadingIcon = Icons.Outlined.Task,
            modifier = Modifier.fillMaxWidth()
        )
        InputKanban(
            text = state.description,
            onTextChange = viewModel::onDescriptionChange,
            label = "Description",
            leadingIcon = Icons.Outlined.Description,
            modifier = Modifier.fillMaxWidth()
        )
//        InputKanban(
//            text = state.priority,
//            onTextChange = viewModel::onPriorityChange,
//            label = "Priority (e.g. important)",
//            leadingIcon = Icons.Outlined.PriorityHigh,
//            modifier = Modifier.fillMaxWidth()
//        )
        // --- ¡CAMBIO 1: Dropdown de Prioridad ---

        // 1. Opciones para el dropdown
        val priorities = listOf("Alto", "Medio", "Bajo")

        // 2. Estado para saber si el menú está expandido
        var isPriorityExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isPriorityExpanded,
            onExpandedChange = { isPriorityExpanded = !isPriorityExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // 3. El "Input" que se muestra
            // (Usamos OutlinedTextField en lugar de InputKanban para que funcione el dropdown)
            OutlinedTextField(
                value = state.priority, // El valor actual del ViewModel
                onValueChange = {}, // No se escribe, solo se selecciona
                readOnly = true, // Evita que se pueda escribir
                label = { Text("Priority") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.PriorityHigh,
                        contentDescription = "Priority Icon"
                    )
                },
                trailingIcon = { // La flecha del dropdown
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Importante para que el menú se ancle aquí
            )

            // 4. El menú que se despliega
            ExposedDropdownMenu(
                expanded = isPriorityExpanded,
                onDismissRequest = { isPriorityExpanded = false }
            ) {
                priorities.forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority) },
                        onClick = {
                            viewModel.onPriorityChange(priority) // Llama al ViewModel
                            isPriorityExpanded = false // Cierra el menú
                        }
                    )
                }
            }
        }

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
