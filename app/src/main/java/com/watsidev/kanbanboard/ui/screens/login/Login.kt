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
    onClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
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
            text = uiState.value.email,
            onTextChange = { viewModel.onEmailChange(it) },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            isEmail = true,
            isNext = true,
            onImeAction = {  },
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.password,
            onTextChange = { viewModel.onPasswordChange(it) },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            isGo = true,
            onImeAction = { viewModel.loginUser() },
//            isError = uiState.value.isPasswordError,
//            errorMessage = uiState.value.errorMessage,
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = stringResource(R.string.sign_in),
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = {
                if (uiState.value.email == "admin" && uiState.value.password == "admin" || uiState.value.email.isEmpty() || uiState.value.password.isEmpty()) onClick() else viewModel.loginUser()// Directly navigate to the main screen if admin credentials are used
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        LaunchedEffect(uiState.value.loginSuccess) {
            if (uiState.value.loginSuccess) {
                onClick()
                viewModel.clearUiState() // Clear the UI state after successful login
            }
        }
        AnimatedVisibility(uiState.value.isLoginError) {
            uiState.value.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
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