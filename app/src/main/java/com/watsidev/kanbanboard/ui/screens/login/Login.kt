package com.watsidev.kanbanboard.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.R
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@Composable
fun LoginScreen(
    // ¡Actualizado! Ahora devuelve el token en el éxito.
    onLoginSuccess: (token: String) -> Unit,
    viewModel: LoginViewModel = viewModel(),
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Este LaunchedEffect maneja la navegación DESPUÉS de que la API responde.
    LaunchedEffect(uiState.token) {
        if (uiState.token != null) {
            onLoginSuccess(uiState.token!!) // <-- Pasa el token
            viewModel.clearUiState() // Limpia el estado después de navegar
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(R.drawable.trellunatext),
            contentDescription = stringResource(R.string.trelluna_logo),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .size(185.dp)
        )
        Text(
            stringResource(R.string.sign_in),
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
        )
        InputKanban(
            text = uiState.email,
            onTextChange = { viewModel.onEmailChange(it) },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            isEmail = true,
            isNext = true,
            onImeAction = { },
            // Conecta los errores de validación del ViewModel
            isError = uiState.isEmailError,
            errorMessage = if (uiState.isEmailError) uiState.errorMessage else null,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.password,
            onTextChange = { viewModel.onPasswordChange(it) },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            isGo = true,
            onImeAction = { viewModel.loginUser() },
            // Conecta los errores de validación del ViewModel
            isError = uiState.isPasswordError,
            errorMessage = if (uiState.isPasswordError) uiState.errorMessage else null,
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = if (uiState.isLoading) "Ingresando..." else stringResource(R.string.sign_in),
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = {
                // ¡CORREGIDO! El botón SIEMPRE llama al ViewModel.
                // El ViewModel maneja si los campos están vacíos o no.
                viewModel.loginUser()
            },
            //enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        // Muestra el error de la API (ej. "Usuario o contraseña incorrectos")
        AnimatedVisibility(uiState.isLoginError && uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "Error desconocido",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.dont_have_account),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Sign Up",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { navigateToSignUp() }
            )
        }
    }
}
