package com.example.metaflow.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metaflow.viewmodel.MainViewModel

@Composable
fun RankingPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()
    val ranking = viewModel.ranking
    val currentUser = viewModel.user
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Seção 1: Líderes do MetaFlow
            RankingSectionHeader(title = "Líderes do MetaFlow")
            
            if (ranking.isNotEmpty()) {
                PodiumSection(ranking)
            } else {
                Text(
                    text = "O pódio está aguardando os primeiros campeões!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Seção 2: Seu Desempenho
            RankingSectionHeader(title = "Seu Desempenho")
            
            currentUser?.let { user ->
                val myRank = ranking.indexOfFirst { it.email == user.email } + 1
                UserHighlightCard(
                    rank = if (myRank > 0) myRank.toString() else "-",
                    name = "Você (${user.name})",
                    status = if (myRank <= 3 && myRank > 0) "No pódio!" else "Continue evoluindo!",
                    points = user.xp.toString()
                )
            }

            // Seção 3: Classificação Global
            RankingSectionHeader(title = "Classificação Global")
            
            if (ranking.size > 3) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ranking.drop(3).forEachIndexed { index, user ->
                        RankingItemCard(
                            position = (index + 4).toString(),
                            name = user.name,
                            points = user.xp.toString()
                        )
                    }
                }
            } else {
                Text(
                    text = "A comunidade está crescendo! Convide novos membros abaixo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Botão para popular o ranking
            if (ranking.size < 5) {
                Button(
                    onClick = { 
                        viewModel.generateCommunity { success ->
                            if (success) {
                                Toast.makeText(context, "Comunidade convidada com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro ao convidar: Verifique as regras do Firestore.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Convidar Membros da Comunidade")
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RankingSectionHeader(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun PodiumSection(ranking: List<com.example.metaflow.model.User>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2º Lugar
        if (ranking.size >= 2) {
            PodiumMember(
                position = "2",
                name = ranking[1].name,
                points = ranking[1].xp.toString(),
                avatarScale = 0.85f,
                blockHeight = 100.dp,
                blockColor = Color(0xFFC0C0C0), // Prata
                textColor = Color(0xFF424242)
            )
        } else {
            EmptyPodiumMember(blockHeight = 70.dp, blockColor = Color(0xFFE0E0E0))
        }

        // 1º Lugar
        if (ranking.isNotEmpty()) {
            PodiumMember(
                position = "1",
                name = ranking[0].name,
                points = ranking[0].xp.toString(),
                avatarScale = 1f,
                blockHeight = 140.dp,
                blockColor = Color(0xFFFFD700), // Dourado
                textColor = Color(0xFF5D4037),
                hasCrown = true
            )
        }

        // 3º Lugar
        if (ranking.size >= 3) {
            PodiumMember(
                position = "3",
                name = ranking[2].name,
                points = ranking[2].xp.toString(),
                avatarScale = 0.85f,
                blockHeight = 80.dp,
                blockColor = Color(0xFFCD7F32), // Bronze
                textColor = Color(0xFF3E2723)
            )
        } else {
            EmptyPodiumMember(blockHeight = 50.dp, blockColor = Color(0xFFF5F5F5))
        }
    }
}

@Composable
fun EmptyPodiumMember(blockHeight: Dp, blockColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier
                .width(80.dp)
                .height(blockHeight),
            color = blockColor,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {}
    }
}

@Composable
fun PodiumMember(
    position: String,
    name: String,
    points: String,
    avatarScale: Float,
    blockHeight: Dp,
    blockColor: Color,
    textColor: Color,
    hasCrown: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            if (hasCrown) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Coroa",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier
                        .size(32.dp)
                        .offset(y = (-24).dp)
                )
            }
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size((60 * avatarScale).dp)
                    .clip(CircleShape)
                    .border(2.dp, blockColor, CircleShape)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = points,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .width(80.dp)
                .height(blockHeight),
            color = blockColor,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = position,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun UserHighlightCard(
    rank: String,
    name: String,
    status: String,
    points: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(36.dp)
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = "$points pontos",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RankingItemCard(
    position: String,
    name: String,
    points: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = position,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(30.dp)
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$points pts",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
