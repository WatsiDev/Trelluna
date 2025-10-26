package com.watsidev.kanbanboard.ui.screens.signUp

import android.util.Log
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
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
fun SignUpScreen(
    // ¡ACTUALIZADO! Ahora devuelve el token
    onRegisterSuccess: (token: String) -> Unit,
    viewModel: SignUpViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // ¡ACTUALIZADO! Se activa con el token
    LaunchedEffect(uiState.token) {
        if (uiState.token != null) {
            onRegisterSuccess(uiState.token!!) // Pasa el token
            viewModel.clearUiState() // Limpia el estado
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
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
            stringResource(R.string.sign_up),
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // --- ¡CORREGIDO! Conectado a 'name' ---
        InputKanban(
            text = uiState.name,
            onTextChange = { viewModel.onNameChange(it) },
            label = "Nombre", // Usar 'name'
            leadingIcon = Icons.Outlined.Person, // Icono consistente
            isNext = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        InputKanban(
            text = uiState.email,
            onTextChange = { viewModel.onEmailChange(it) },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            isEmail = true,
            isNext = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        InputKanban(
            text = uiState.password,
            onTextChange = { viewModel.onPasswordChange(it) },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            isNext = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.confirmPassword,
            onTextChange = { viewModel.onConfirmPasswordChange(it) },
            label = stringResource(R.string.confirm_password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            isGo = true,
            onImeAction = { viewModel.registerUser() },
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = if (uiState.isLoading) "Creando cuenta..." else stringResource(R.string.sign_up),
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = {
                viewModel.registerUser()
            },
            //enabled = !uiState.isLoading, // Deshabilitar mientras carga
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        // --- ¡AÑADIDO! Bloque de error ---
        AnimatedVisibility(uiState.isError && uiState.errorMessage != null) {
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
                stringResource(R.string.already_have_account),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                stringResource(R.string.sign_in),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { navigateToLogin() }
            )
        }
    }
}
