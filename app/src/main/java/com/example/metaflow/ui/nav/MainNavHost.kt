package com.example.metaflow.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metaflow.ui.GoalsPage
import com.example.metaflow.ui.HistoryPage
import com.example.metaflow.ui.HomePage
import com.example.metaflow.ui.ProfilePage
import com.example.metaflow.ui.ProgressPage
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            HomePage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable<Route.Goals> {
            GoalsPage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable<Route.Progress> {
            ProgressPage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable<Route.History> {
            HistoryPage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable<Route.Profile> {
            ProfilePage(
                modifier = modifier,
                viewModel = viewModel,
                onLogout = onLogout
            )
        }
    }
}