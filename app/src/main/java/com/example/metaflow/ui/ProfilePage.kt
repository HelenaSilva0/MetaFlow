package com.example.metaflow.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.metaflow.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val user = viewModel.user
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember(user?.name) { mutableStateOf(user?.name ?: "") }

    val themeOptions = listOf("Sistema", "Claro", "Escuro")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = convertUriToBase64(context, it)
            if (base64 != null) {
                viewModel.updateProfilePic(base64)
            }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Perfil",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            if (!isEditing) {
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Perfil",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
                if (isEditing) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.Badge, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { 
                                isEditing = false
                                editedName = user?.name ?: ""
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                viewModel.updateUserProfile(editedName)
                                isEditing = false
                            }
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Salvar")
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.profilePic != null) {
                                val imageBytes = Base64.decode(user.profilePic, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                AsyncImage(
                                    model = bitmap,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = (user?.name?.take(1) ?: "M").uppercase(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp).padding(bottom = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Column {
                            Text(
                                text = user?.name ?: "Usuário MetaFlow",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.size(4.dp))
                                Text(
                                    text = user?.email ?: "carregando email...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatsColumn(
                            icon = Icons.Default.Star,
                            iconColor = Color(0xFFFFD700),
                            value = "${user?.xp ?: 0}",
                            label = "XP"
                        )
                        StatsColumn(
                            icon = Icons.Default.Whatshot,
                            iconColor = Color(0xFFFF5722),
                            value = "${user?.streak ?: 0}",
                            label = "Streak"
                        )
                        StatsColumn(
                            icon = Icons.Default.EmojiEvents,
                            iconColor = Color(0xFF4CAF50),
                            value = "${user?.totalCompleted ?: 0}",
                            label = "Metas"
                        )
                    }
                }
            }
        }

        Text(
            text = "Insígnias Conquistadas",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (user?.badges.isNullOrEmpty()) {
                Text(
                    text = "Você ainda não conquistou insígnias. Complete metas para ganhar!",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    user?.badges?.forEach { badgeId ->
                        BadgeIcon(badgeId)
                    }
                }
            }
        }

        Text(
            text = "Configurações",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificações",
                    subtitle = "Ativadas"
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.size(16.dp))
                        Text(
                            text = "Tema do App",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        themeOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
                                onClick = { viewModel.updateTheme(label) },
                                selected = label == (user?.theme ?: "Sistema")
                            ) {
                                Text(label, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text(text = "Sair da conta")
        }
    }
}

@Composable
fun StatsColumn(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadgeIcon(badgeId: String) {
    val (icon, color, title) = when (badgeId) {
        "primeira_meta" -> Triple(Icons.Default.Star, Color(0xFF4CAF50), "Iniciante")
        "bronze_meta" -> Triple(Icons.Default.EmojiEvents, Color(0xFFCD7F32), "Bronze")
        "prata_meta" -> Triple(Icons.Default.EmojiEvents, Color(0xFFC0C0C0), "Prata")
        "ouro_meta" -> Triple(Icons.Default.EmojiEvents, Color(0xFFFFD700), "Ouro")
        else -> Triple(Icons.Default.Star, Color.Gray, "Badge")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.size(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun convertUriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        
        // Redimensionar para não exceder limites do Firestore (max 1MB por documento)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
        
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
