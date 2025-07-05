package com.watsidev.kanbamboard.ui.screens.signUp

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbamboard.R
import com.watsidev.kanbamboard.ui.common.ButtonKanban
import com.watsidev.kanbamboard.ui.common.InputKanban


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
            contentDescription = "Trelluna Logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .size(185.dp)

        )
        Text(
            "Sign Up",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
        )
        InputKanban(
            text = uiState.value.email,
            onTextChange = { viewModel.onEmailChange(it) },
            label = "Email",
            leadingIcon = Icons.Outlined.Email,
            isPassword = false,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.username,
            onTextChange = { viewModel.onUsernameChange(it) },
            label = "Username",
            leadingIcon = Icons.Outlined.AccountBox, // You can change this icon if needed
            isPassword = false,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.password,
            onTextChange = { viewModel.onPasswordChange(it) },
            label = "Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = uiState.value.confirmPassword,
            onTextChange = { viewModel.onConfirmPasswordChange(it) },
            label = "Confirm Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = "Sign Up",
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = {
                viewModel.clearUiState()
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Already have an account?",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Sign In",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable{ navigateToLogin() }
            )
        }
    }
}