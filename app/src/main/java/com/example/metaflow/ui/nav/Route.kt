package com.example.metaflow.ui.nav

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Ranking : Route

    @Serializable
    data object Progress : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Profile : Route
}
