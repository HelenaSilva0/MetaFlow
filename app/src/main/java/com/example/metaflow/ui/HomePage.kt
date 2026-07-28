package com.example.metaflow.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.metaflow.model.Mission
import com.example.metaflow.ui.components.GoalItem
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val progress = viewModel.progressPercent()
    val goals = viewModel.goals
    val missions = viewModel.missions
    val activity = LocalActivity.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Launcher para permissão de notificação (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "As notificações foram desativadas para o MetaFlow.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Hábitos alcançam metas.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleMedium
        )

        // Seção de Missões
        if (missions.isNotEmpty()) {
            Text(
                text = "Missões de Hoje",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            missions.forEach { mission ->
                MissionItem(mission)
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Progresso de hoje",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress / 100f },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$progress% das metas concluídas",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Resumo",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Metas cadastradas: ${viewModel.totalCount()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Metas concluídas: ${viewModel.completedCount()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "XP acumulado: ${viewModel.xpPoints()} XP",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Text(
            text = "Minhas Metas",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (goals.isEmpty()) {
            Text(
                text = "Você ainda não tem metas cadastradas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            goals.forEach { goal ->
                GoalItem(
                    goal = goal,
                    onClick = {
                        viewModel.toggleGoal(goal)
                        Toast.makeText(
                            activity,
                            "Meta atualizada: ${goal.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onClose = {
                        viewModel.removeGoal(goal)
                        Toast.makeText(
                            activity,
                            "Meta removida: ${goal.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onToggleMonitored = { newValue ->
                        // Verifica permissão se estiver tentando ativar
                        if (newValue && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        
                        // Atualiza a meta (o monitoramento só agendará no Worker se a permissão existir no disparo)
                        viewModel.updateGoal(goal.copy(isMonitored = newValue))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Continue firme. Pequenos hábitos criam grandes resultados.",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MissionItem(mission: Mission) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (mission.isCompleted) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.Flag,
                contentDescription = null,
                tint = if (mission.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${mission.currentCount}/${mission.targetCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "+${mission.points} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

