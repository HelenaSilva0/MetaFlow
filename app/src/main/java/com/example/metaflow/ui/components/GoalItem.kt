package com.example.metaflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.metaflow.model.Goal

@Composable
fun GoalItem(
    goal: Goal,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleMonitored: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (goal.completed) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Outlined.RadioButtonUnchecked
                },
                contentDescription = "Status da meta",
                tint = if (goal.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goal.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    val icon = if (goal.isMonitored) Icons.Filled.Notifications else Icons.Outlined.Notifications
                    val iconModifier = Modifier.size(24.dp).let {
                        if (onToggleMonitored != null) {
                            it.clickable {
                                onToggleMonitored(!goal.isMonitored)
                            }
                        } else it
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "Monitorada?",
                        tint = if (goal.isMonitored) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = iconModifier
                    )
                }
                Text(
                    text = "${goal.category} • ${goal.reminderTime} • ${goal.priority}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (goal.recurrence.isNotBlank() || goal.location.isNotBlank()) {
                    Text(
                        text = "${goal.recurrence}${if (goal.location.isNotBlank()) " @ ${goal.location}" else ""}",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            IconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remover meta",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
