package com.example.metaflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun HistoryPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Histórico",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Resumo das suas metas e hábitos recentes.",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        HistoryCard(
            title = "Hoje",
            description = "${viewModel.completedCount()} metas concluídas de ${viewModel.totalCount()}"
        )

        HistoryCard(
            title = "Esta semana",
            description = "Você manteve uma boa sequência de hábitos."
        )

        HistoryCard(
            title = "Este mês",
            description = "Seu desempenho geral está em ${viewModel.progressPercent()}%."
        )
    }
}

@Composable
fun HistoryCard(
    title: String,
    description: String
) {
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
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
