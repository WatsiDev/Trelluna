package com.watsidev.kanbanboard.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.watsidev.kanbanboard.R
import com.watsidev.kanbanboard.ui.ServerState
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.InputKanban

@Composable
fun LoginScreen(
    serverState: ServerState,
    onRetry: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFormEnabled = serverState == ServerState.Ready

    // Este LaunchedEffect maneja la navegación DESPUÉS de que la API responde.
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess() // <-- ¡Ya no pasa el token!
            viewModel.clearUiState() // Limpia el estado después de navegar
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                //enabled = isFormEnabled, // <-- CAMBIO AÑADIDO
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
                //enabled = isFormEnabled, // <-- CAMBIO AÑADIDO
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
                    viewModel.loginUser()
                },
                // <-- CAMBIO CLAVE: Habilitado solo si el form está listo Y no está cargando el login
                //enabled = isFormEnabled && !uiState.isLoading,
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

        // --- INICIO DEL CAMBIO PRINCIPAL: LÓGICA DEL MODAL ---
        // Usamos un 'when' para mostrar el modal correcto
        when (serverState) {

            // (Asumiendo que creaste InitialLoading como se discutió)
            is ServerState.InitialLoading, is ServerState.WakingUp -> {
                WakingUpServer()
            }

            is ServerState.NetworkError -> {
                ErrorStateView(
                    icon = Icons.Default.CloudOff,
                    message = "Sin conexión a internet",
                    onRetryClick = onRetry // Le pasamos la función
                )
            }

            is ServerState.ServerError -> {
                ErrorStateView(
                    icon = Icons.Default.Warning,
                    message = "El servidor no responde",
                    onRetryClick = onRetry // Le pasamos la función
                )
            }

            // Cuando está listo, no mostramos ningún modal
            is ServerState.Ready -> {
            }
        }
        // --- FIN DEL CAMBIO ---
    }
}

@Composable
fun WakingUpServer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                //.padding(horizontal = 18.dp)
                .size(300.dp)

            ,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectingToServerAnimation()
            Text(stringResource(R.string.connect_server), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ConnectingToServerAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.connnecting))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .size(200.dp)
    )
}

/**
 * NUEVO: Un Composable para mostrar errores, con un estilo
 * idéntico al de tu WakingUpServer.
 */
@Composable
fun ErrorStateView(
    icon: ImageVector,
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f)), // Fondo semitransparente
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer) // Fondo del modal
                .padding(24.dp)
                .sizeIn(minWidth = 300.dp, minHeight = 300.dp), // Tamaño idéntico
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = message,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reintentar")
            }
        }
    }
}