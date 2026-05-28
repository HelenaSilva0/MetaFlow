package com.example.metaflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun ProgressPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val progress = viewModel.progressPercent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF120026))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Progresso",
            color = Color.White,
            fontSize = 26.sp
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF24103F)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "$progress%",
                    color = Color.White,
                    fontSize = 32.sp
                )

                Text(
                    text = "desempenho geral",
                    color = Color(0xFFD98CFF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF24103F)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Metas concluídas",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Text(
                    text = "${viewModel.completedCount()} de ${viewModel.totalCount()} metas",
                    color = Color(0xFFD98CFF)
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF24103F)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Gamificação",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Text(
                    text = "XP atual: ${viewModel.xpPoints()} pontos",
                    color = Color(0xFFD98CFF)
                )

                Text(
                    text = "Sequência: 3 dias cumpridos",
                    color = Color(0xFFD98CFF)
                )
            }
        }
    }
}