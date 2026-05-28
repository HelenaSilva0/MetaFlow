package com.example.metaflow.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Goals : Route

    @Serializable
    data object Progress : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Profile : Route
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton :
        BottomNavItem("Início", Icons.Default.Home, Route.Home)

    data object GoalsButton :
        BottomNavItem("Metas", Icons.Default.CheckCircle, Route.Goals)

    data object ProgressButton :
        BottomNavItem("Progresso", Icons.Default.Favorite, Route.Progress)

    data object HistoryButton :
        BottomNavItem("Histórico", Icons.Default.DateRange, Route.History)

    data object ProfileButton :
        BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
}