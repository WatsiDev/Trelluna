package com.watsidev.kanbanboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.watsidev.kanbanboard.R
import com.watsidev.kanbanboard.ui.navigation.Home
import com.watsidev.kanbanboard.ui.navigation.Login
import com.watsidev.kanbanboard.ui.navigation.SignUp
import com.watsidev.kanbanboard.ui.screens.home.HomeScreen
import com.watsidev.kanbanboard.ui.screens.login.LoginScreen
import com.watsidev.kanbanboard.ui.screens.signUp.SignUpScreen

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onClick = { navController.navigate(Home) },
                navigateToSignUp = { navController.navigate(SignUp) },
                modifier = Modifier
            )
        }
        composable<SignUp>{
            SignUpScreen(
                onClick = { navController.navigate(Home) },
                navigateToLogin = { navController.popBackStack<Login>(inclusive = false) },
                modifier = Modifier
            )
        }
        composable<Home> {
            ScreenWithTopBar { pd ->
                HomeScreen(
                    modifier = Modifier.padding(pd)
                )
            }
        }
    }
}

@Composable
fun ScreenWithTopBar(content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            KanbanTopBar()
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { pd ->
        content(pd)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanTopBar() {
    TopAppBar(
        title = {
            Image(
                painterResource(R.drawable.trellunahorizontal),
                contentDescription = "Logo App",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .size(164.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        actions = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Menu Icon",
                )
            }
        }
    )
}