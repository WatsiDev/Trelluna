package com.watsidev.kanbanboard.ui.screens.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import com.watsidev.kanbanboard.model.network.Project
import com.watsidev.kanbanboard.ui.common.TaskCard
import com.watsidev.kanbanboard.ui.common.TaskTitle

@Composable
fun ProjectsScreen( // <-- Renombrado para coincidir con tu archivo
    modifier: Modifier = Modifier, // <-- Añadido el modifier
    // Asumo que usas Hilt o la factoría por defecto para obtener el ViewModel
    viewModel: ProjectViewModel = viewModel(),
    onNewProject: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    // Observa el UiState del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Carga los proyectos la primera vez que la pantalla se compone
    LaunchedEffect(key1 = Unit) {
        viewModel.loadProjects()
    }

    Box(
        modifier = modifier.fillMaxSize(), // Usa el modifier
        contentAlignment = Alignment.Center
    ) {
        // --- Contenido Principal ---
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. Título de la Tarea (Combinando tu estilo y nuestra lógica)
            TaskTitle(
                backgroundColor = MaterialTheme.colorScheme.primary, // De tu archivo
                badgeColor = MaterialTheme.colorScheme.primary,    // De tu archivo
                badgeText = uiState.projectList.size.toString(),     // Dinámico
                text = "Projects",                                 // De tu archivo
                onAddClick = onNewProject,                         // Funcional
                //modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Lista Perezosa (LazyColumn) de Proyectos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.projectList,
                    key = { project -> project.id } // Key para mejor rendimiento
                ) { project ->

                    // TaskCard (Combinando tu estilo y nuestra lógica)
                    TaskCard(
                        // title = nombre del proyecto
                        title = project.name,
                        // description = descripcion del proyecto
                        description = project.description ?: "Sin descripción", // De tu archivo
                        // comments y cheked que se queden como cadenas vacias
                        comments = "",
                        checked = "",
                        // chipText = (el numero del proyecto, ejemplo: 1)
                        chipText = project.id.toString(),
                        chipTextColor = MaterialTheme.colorScheme.primary, // De tu archivo
                        // Añadí un onClick para la navegación
                        modifier = Modifier.clickable{ onProjectClick(project.id) },
                        //onClick = { onProjectClick(project) }
                    )
                }
            }
        }

        // --- Estado de Carga ---
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        // --- Estado de Error ---
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}