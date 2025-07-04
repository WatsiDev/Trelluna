package com.watsidev.kanbamboard.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.watsidev.kanbamboard.R
import com.watsidev.kanbamboard.ui.navigation.Home
import com.watsidev.kanbamboard.ui.navigation.Login
import com.watsidev.kanbamboard.ui.screens.Home.HomeScreen
import com.watsidev.kanbamboard.ui.screens.Login.LoginScreen

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