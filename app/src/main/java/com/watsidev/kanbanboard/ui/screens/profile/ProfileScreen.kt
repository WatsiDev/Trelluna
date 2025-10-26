package com.watsidev.kanbanboard.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban
import com.watsidev.kanbanboard.ui.common.TaskTitle

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    // Asumimos que recibes esto de la navegación.
    // Pasa 'null' si quieres cargar el perfil propio (/auth/me)
    // Pasa el ID si eres admin y ves otro perfil
    userIdToLoad: Int? = null,
    // Asumimos que tienes el token disponible (ej. desde un ViewModel de sesión)
    authToken: String
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Cargar el perfil cuando la pantalla aparece por primera vez
    LaunchedEffect(authToken, userIdToLoad) {
        if (authToken.isNotBlank()) {
            viewModel.loadProfile(authToken, userIdToLoad)
        }
    }

    // Limpiar flags de éxito si la pantalla se recompone (ej. después de un error)
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            viewModel.clearSuccessFlags()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Tu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // --- Manejo de Carga y Error ---
        if (state.isLoading && state.profile == null) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // --- Formulario de Perfil ---
        if (state.profile != null) {
            val profile = state.profile!!

            InputKanban(
                text = state.formName,
                onTextChange = viewModel::onNameChange,
                label = "Nombre",
                leadingIcon = Icons.Outlined.Person,
                modifier = Modifier.fillMaxWidth()
            )

            InputKanban(
                text = state.formEmail,
                onTextChange = viewModel::onEmailChange,
                label = "Email",
                leadingIcon = Icons.Outlined.Email,
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            InputKanban(
                text = state.formRole,
                onTextChange = viewModel::onRoleChange,
                label = "Rol",
                leadingIcon = Icons.Outlined.AdminPanelSettings,
                // Opcional: deshabilita si no es admin
                // enabled = esAdmin,
                modifier = Modifier.fillMaxWidth()
            )

            ButtonKanban(
                text = if (state.isLoading) "Guardando..." else "Guardar Cambios",
                icon = Icons.Outlined.Save,
                onClick = { viewModel.updateProfile(authToken, profile.id) },
                //enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.updateSuccess) {
                Text(
                    "¡Perfil actualizado con éxito!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Formulario de Contraseña ---
            Text(
                "Cambiar Contraseña",
                style = MaterialTheme.typography.titleLarge
            )

            InputKanban(
                text = state.formPassword,
                onTextChange = viewModel::onPasswordChange,
                label = "Nueva Contraseña",
                leadingIcon = Icons.Outlined.Lock,
                //visualTransformation = PasswordVisualTransformation(),
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            ButtonKanban(
                text = if (state.isLoading) "Actualizando..." else "Actualizar Contraseña",
                icon = Icons.Outlined.Lock,
                onClick = { viewModel.changePassword(authToken, profile.id) },
                //enabled = !state.isLoading && state.formPassword.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.passwordSuccess) {
                Text(
                    "¡Contraseña actualizada!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Zona de Peligro ---
            Text(
                "Zona de Peligro",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )

            ButtonKanban(
                text = if (state.isLoading) "Eliminando..." else "Eliminar Cuenta",
                icon = Icons.Outlined.DeleteForever,
                onClick = { viewModel.deleteAccount(authToken, profile.id) },
                //enabled = !state.isLoading,
                // Cambia los colores para que sea un botón de peligro
                //containerColor = MaterialTheme.colorScheme.errorContainer,
                //contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.deleteSuccess) {
                Text(
                    "Cuenta eliminada.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
