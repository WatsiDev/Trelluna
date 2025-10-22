package com.watsidev.kanbanboard.ui.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@Composable
fun CreateProjectScreen(
    modifier: Modifier = Modifier,
    // Reutilizamos el ProjectViewModel que ya tiene la lógica del formulario
    viewModel: ProjectViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Resetea el estado del formulario y eventos al entrar a la pantalla
    LaunchedEffect(key1 = Unit) {
        viewModel.clearProjectForm()
        viewModel.eventConsumed() // Limpia eventos pasados (como el de éxito)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Añadí padding para que no se pegue a los bordes
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Crear nuevo proyecto", // Título de la pantalla
            style = MaterialTheme.typography.titleLarge
        )

        // Campo para el Nombre del Proyecto
        InputKanban(
            text = state.formProjectName,
            onTextChange = { viewModel.onProjectNameChange(it) },
            label = "Nombre del Proyecto",
            leadingIcon = Icons.Outlined.Dashboard, // Icono de referencia
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para la Descripción del Proyecto
        InputKanban(
            text = state.formProjectDescription,
            onTextChange = { viewModel.onProjectDescriptionChange(it) },
            label = "Descripción (Opcional)",
            leadingIcon = Icons.Outlined.Description, // Icono diferente
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error (prioriza error de formulario)
        val errorMessage = state.formError ?: state.errorMessage
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // Botón para crear el proyecto
        ButtonKanban(
            text = if (state.isLoading) "Creando..." else "Crear Proyecto",
            icon = Icons.Outlined.Create,
            onClick = { viewModel.createProject() }, // Llama a la función del VM
            modifier = Modifier.fillMaxWidth(),
            // Habilita/Deshabilita el botón si está cargando
            //enabled = !state.isLoading
        )

        // Mensaje de éxito (basado en el evento)
        if (state.lastEvent is ProjectEvent.ProjectCreated) {
            Text(
                "¡Proyecto creado con éxito!",
                color = MaterialTheme.colorScheme.primary
            )
            // El VM limpia el formulario automáticamente al tener éxito
        }
    }
}