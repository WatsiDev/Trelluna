package com.watsidev.kanbanboard.ui.screens.signUp

import android.util.Log
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
    onClick: () -> Unit,
    viewModel: SignUpViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
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
        InputKanban(
            text = uiState.value.email,
            onTextChange = { viewModel.onEmailChange(it) },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            isPassword = false,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.username,
            onTextChange = { viewModel.onUsernameChange(it) },
            label = stringResource(R.string.username),
            leadingIcon = Icons.Outlined.AccountBox, // You can change this icon if needed
            isPassword = false,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.password,
            onTextChange = { viewModel.onPasswordChange(it) },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.confirmPassword,
            onTextChange = { viewModel.onConfirmPasswordChange(it) },
            label = stringResource(R.string.confirm_password),
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = stringResource(R.string.sign_up),
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = {
                viewModel.registerUser()
                Log.d("SignUpScreen", "Register completed with email: ${uiState.value.email} - ${uiState.value.registrationCompleted}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        Log.d("SignUpScreen", "UI State: ${uiState.value.registrationCompleted}")
        LaunchedEffect(uiState.value.registrationCompleted) {
            if (uiState.value.registrationCompleted) {
                onClick()
                viewModel.clearUiState() // Clear the state after successful registration
            }
        }
        uiState.value.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        uiState.value.successMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
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
                    .clickable{ navigateToLogin() }
            )
        }
    }
}
