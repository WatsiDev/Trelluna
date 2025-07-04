package com.watsidev.kanbamboard.ui.screens.Login

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watsidev.kanbamboard.R
import com.watsidev.kanbamboard.ui.common.ButtonKanban
import com.watsidev.kanbamboard.ui.common.InputKanban

@Composable
fun LoginScreen(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 18.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(R.drawable.trellunatext),
            contentDescription = "Trelluna Logo",
            modifier = Modifier
                .size(185.dp)
        )
        Text(
            "Log In",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light
        )
        InputKanban(
            text = "",
            onTextChange = {},
            label = "Email",
            modifier = Modifier
                .fillMaxWidth()
        )
        InputKanban(
            text = "",
            onTextChange = {},
            label = "Password",
            modifier = Modifier
                .fillMaxWidth()
        )
        ButtonKanban(
            text = "Login",
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = { onClick() },
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
                "Don't have an account?",
                fontSize = 16.sp,
            )
            Text(
                "Sign Up",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable{  }
            )
        }
    }
}