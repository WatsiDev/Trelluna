package com.watsidev.kanbanboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.watsidev.kanbanboard.R
import com.watsidev.kanbanboard.model.session.SessionManager
import com.watsidev.kanbanboard.ui.navigation.Home
import com.watsidev.kanbanboard.ui.navigation.Login
import com.watsidev.kanbanboard.ui.navigation.NewColumn
import com.watsidev.kanbanboard.ui.navigation.NewProject
import com.watsidev.kanbanboard.ui.navigation.NewTask
import com.watsidev.kanbanboard.ui.navigation.Profile
import com.watsidev.kanbanboard.ui.navigation.Projects
import com.watsidev.kanbanboard.ui.navigation.SignUp
import com.watsidev.kanbanboard.ui.navigation.items
import com.watsidev.kanbanboard.ui.screens.home.CreateColumnScreen
import com.watsidev.kanbanboard.ui.screens.home.CreateTaskScreen
import com.watsidev.kanbanboard.ui.screens.home.HomeScreen
import com.watsidev.kanbanboard.ui.screens.login.LoginScreen
import com.watsidev.kanbanboard.ui.screens.profile.ProfileScreen
import com.watsidev.kanbanboard.ui.screens.projects.CreateProjectScreen
import com.watsidev.kanbanboard.ui.screens.projects.ProjectsScreen
import com.watsidev.kanbanboard.ui.screens.signUp.SignUpScreen
import kotlinx.coroutines.launch

@Composable
fun App(
    serverState: ServerState
) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    var authToken by rememberSaveable { mutableStateOf<String?>(null) }

    val onNavigateFromMenu = { route: Any ->
        if (route == Login) {
            authToken = null
            SessionManager.clearSession()
            navController.navigate(Login) {
                popUpTo(Login) { inclusive = true }
            }
        } else {
            navController.navigate(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                serverState = serverState,
                onRetry = {
                    mainViewModel.retryConnection()
                },
                onLoginSuccess = {
                    authToken = SessionManager.getAuthToken()
                    navController.navigate(Projects) {
                    //    popUpTo(Login) { inclusive = true }
                    }
                },
                navigateToSignUp = { navController.navigate(SignUp) },
                modifier = Modifier
            )
        }
        composable<SignUp> {
            SignUpScreen(
                onRegisterSuccess = {
                    authToken = SessionManager.getAuthToken()
                    navController.navigate(Projects) {
                        popUpTo(SignUp) { inclusive = true }
                    }
                },
                navigateToLogin = { navController.popBackStack<Login>(inclusive = false) },
                modifier = Modifier
            )
        }

        // --- 4. USAMOS LA NUEVA LÓGICA "onNavigateFromMenu" EN TODAS LAS PANTALLAS ---
        composable<Home> {
            val home: Home = it.toRoute()
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                HomeScreen(
                    projectId = home.id,
                    onCreateNewColumn = { id -> navController.navigate(NewColumn(id)) },
                    onProjectDeleted = { navController.popBackStack() },
                    createTask = { id -> navController.navigate(NewTask(id)) },
                    modifier = Modifier.padding(pd)
                )
            }
        }
        composable<NewColumn> {
            val newColumn: NewColumn = it.toRoute()
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                CreateColumnScreen(
                    modifier = Modifier.padding(pd),
                    projectId = newColumn.id,
                    onColumnCreated = { navController.popBackStack<Home>(inclusive = false) }
                )
            }
        }
        composable<NewTask> {
            val newTask: NewTask = it.toRoute()
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                CreateTaskScreen(
                    id = newTask.id,
                    modifier = Modifier.padding(pd)
                )
            }
        }
        composable<Profile> {
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                ProfileScreen(
                    onLogout = { onNavigateFromMenu(Login) },
                    modifier = Modifier.padding(pd)
                )
            }
        }
        composable<Projects> {
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                ProjectsScreen(
                    onNewProject = { navController.navigate(NewProject) },
                    onProjectClick = { id -> navController.navigate(Home(id)) },
                    modifier = Modifier.padding(pd)
                )
            }
        }
        composable<NewProject> {
            ScreenWithTopBar(
                onClick = onNavigateFromMenu // <-- ¡CAMBIO!
            ) { pd ->
                CreateProjectScreen(
                    modifier = Modifier.padding(pd)
                )
            }
        }
    }
}

@Composable
fun ScreenWithTopBar(
    onClick: (Any) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideSheet(
                onClick = {
                    scope.launch { drawerState.close() }
                    onClick(it)
                }
            )
        },
    ) {
        Scaffold(
            topBar = {
                KanbanTopBar(onMenuClick = { scope.launch { drawerState.open() } })
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { pd ->
            content(pd)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanTopBar(
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Image(
                painterResource(R.drawable.trellunahorizontal),
                contentDescription = stringResource(R.string.trelluna_logo),
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
                onClick = { onMenuClick() },
            ) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.menu_icon),
                )
            }
        }
    )
}

@Composable
fun SideSheet(
    onClick: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(280.dp)
    ) {
        Image(
            painterResource(R.drawable.trellunatext),
            contentDescription = stringResource(R.string.trelluna_logo),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title) },
                icon = {
                    Icon(
                        item.icons,
                        contentDescription = item.title,
                    )
                },
                selected = false,
                onClick = { onClick(item.route) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
