package com.watsidev.kanbanboard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object Home

@Serializable
object NewColumn

@Serializable
data class NewTask(
    val id: Int
)

@Serializable
object Profile

@Serializable
object Projects

data class NavigationItem(
    val title: String,
    val route: Any,
    val icons: ImageVector
)

val items = listOf(
    NavigationItem(
        title = "Home",
        route = Home,
        icons = Icons.Outlined.Home
    ),
    NavigationItem(
        title = "Projects",
        route = Projects,
        icons = Icons.Outlined.SpaceDashboard
    ),
    NavigationItem(
        title = "Profile",
        route = Profile,
        icons = Icons.Outlined.AccountCircle
    ),
    NavigationItem(
        title = "LogOut",
        route = Login,
        icons = Icons.AutoMirrored.Outlined.Logout
    )
)