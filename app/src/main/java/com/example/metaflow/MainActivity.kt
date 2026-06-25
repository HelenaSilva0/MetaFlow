package com.example.metaflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.metaflow.ui.GoalDialog
import com.example.metaflow.ui.nav.BottomNavBar
import com.example.metaflow.ui.nav.BottomNavItem
import com.example.metaflow.ui.nav.MainNavHost
import com.example.metaflow.ui.theme.MetaFlowTheme
import com.example.metaflow.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }

            MetaFlowTheme {
                if (showDialog) {
                    GoalDialog(
                        onDismiss = {
                            showDialog = false
                        },
                        onConfirm = { name, category, reminderTime, priority ->
                            if (name.isNotBlank()) {
                                viewModel.addGoal(
                                    name = name,
                                    category = category.ifBlank { "Geral" },
                                    reminderTime = reminderTime.ifBlank { "--:--" },
                                    priority = priority.ifBlank { "Média" }
                                )
                            }

                            showDialog = false
                        }
                    )
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "MetaFlow",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.GoalsButton,
                            BottomNavItem.ProgressButton,
                            BottomNavItem.HistoryButton,
                            BottomNavItem.ProfileButton
                        )

                        BottomNavBar(
                            navController = navController,
                            items = items
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                showDialog = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar meta"
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel,
                            onLogout = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
