package com.example.metaflow.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metaflow.ui.HistoryPage
import com.example.metaflow.ui.HomePage
import com.example.metaflow.ui.LoginPage
import com.example.metaflow.ui.ProfilePage
import com.example.metaflow.ui.ProgressPage
import com.example.metaflow.ui.RankingPage
import com.example.metaflow.ui.RegisterPage
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Login,
    onLogout: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Login> {
            LoginPage(
                viewModel = viewModel,
                onLogin = { email, password ->
                    viewModel.login(email, password) { success ->
                        if (success) onLoginSuccess()
                    }
                },
                onRegister = {
                    navController.navigate(Route.Register)
                }
            )
        }

        composable<Route.Register> {
            RegisterPage(
                viewModel = viewModel,
                onRegister = { name, email, password ->
                    viewModel.register(name, email, password) { success ->
                        if (success) onLoginSuccess()
                    }
                }
            )
        }

        composable<Route.Home> {
            HomePage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable<Route.Ranking> {
            RankingPage(
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
