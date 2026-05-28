package com.example.metaflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF120026))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Perfil",
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
                    text = "Usuário MetaFlow",
                    color = Color.White,
                    fontSize = 20.sp
                )

                Text(
                    text = "metaflow@email.com",
                    color = Color(0xFFD98CFF)
                )

                Text(
                    text = "XP: ${viewModel.xpPoints()} pontos",
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
                    text = "Configurações",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Text(
                    text = "Notificações ativadas",
                    color = Color(0xFFD98CFF)
                )

                Text(
                    text = "Tema: roxo escuro",
                    color = Color(0xFFD98CFF)
                )
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sair")
        }
    }
}