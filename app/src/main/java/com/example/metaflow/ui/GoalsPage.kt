package com.example.metaflow.ui

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metaflow.ui.components.GoalItem
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun GoalsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val goals = viewModel.goals
    val activity = LocalActivity.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Minhas metas",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Toque em uma meta para marcar como concluída.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        LazyColumn(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(goals, key = { it.id }) { goal ->
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
                    }
                )
            }
        }
    }
}
