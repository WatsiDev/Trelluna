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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

/**
 * Pantalla dedicada a crear una nueva columna dentro de un proyecto específico.
 *
 * @param projectId El ID del proyecto al que se añadirá la columna (viene de navegación).
 * @param onColumnCreated Callback para navegar hacia atrás una vez creada la columna.
 * @param viewModel El ViewModel que maneja la lógica de columnas.
 */
@Composable
fun CreateColumnScreen(
    modifier: Modifier = Modifier,
    projectId: Int,
    onColumnCreated: () -> Unit,
    viewModel: ColumnViewModel = viewModel() // Usamos el ViewModel que creamos
) {
    val state by viewModel.uiState.collectAsState()

    // 1. Informa al ViewModel del ID del proyecto actual en cuanto la pantalla se carga.
    // Usamos 'Unit' para que solo se ejecute una vez.
    LaunchedEffect(Unit) {
        viewModel.onProjectIdChange(projectId.toString())
        // Limpiamos el nombre por si había algo escrito de antes
        viewModel.onNameChange("")
        // Reseteamos el estado de éxito por si acaso
        viewModel.clearSuccessFlag()
    }

    // 2. Observa el estado de 'operationSuccess'. Si cambia a 'true',
    // significa que la columna se creó y navegamos hacia atrás.
    LaunchedEffect(state.operationSuccess) {
        if (state.operationSuccess) {
            onColumnCreated()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // Añadido padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Añadir nueva columna",
            style = MaterialTheme.typography.headlineSmall // Título mejorado
        )

        // 3. Campo de texto solo para el nombre
        InputKanban(
            text = state.formName, // Usamos 'formName' del nuevo UiState
            onTextChange = { viewModel.onNameChange(it) },
            label = "Nombre de la columna",
            leadingIcon = Icons.Outlined.Dashboard,
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Muestra un indicador de carga
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        // 5. Muestra mensajes de error
        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            // Opcional: Ocultar el error después de un tiempo
            LaunchedEffect(it) {
                kotlinx.coroutines.delay(4000)
                viewModel.clearErrorMessage()
            }
        }

        // 6. Botón de crear
        ButtonKanban(
            text = if (state.isLoading) "Creando..." else "Crear Columna",
            icon = Icons.Outlined.Create,
            onClick = {
                // El ViewModel ya sabe el projectId (del LaunchedEffect)
                // y el formName (del InputKanban)
                viewModel.createColumn(projectId)
            },
            //enabled = !state.isLoading, // Deshabilita el botón mientras carga
            modifier = Modifier.fillMaxWidth()
        )

        // No mostramos el texto de éxito, ya que navegamos
        // hacia atrás automáticamente.
    }
}