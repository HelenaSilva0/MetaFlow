package com.example.metaflow.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun GoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        category: String,
        reminderTime: String,
        priority: String,
        recurrence: String,
        deadline: String,
        location: String,
        latitude: Double?,
        longitude: Double?
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var showMapPicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showMapPicker) {
        MapPickerDialog(
            onDismiss = { showMapPicker = false },
            onLocationSelected = { latLng, address ->
                latitude = latLng.latitude
                longitude = latLng.longitude
                locationName = address
                showMapPicker = false
            }
        )
    }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Criar nova meta",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fechar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Nome da meta") },
                    value = name,
                    onValueChange = { name = it },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Categoria") },
                    value = category,
                    onValueChange = { category = it },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Horário do lembrete") },
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Prioridade") },
                    value = priority,
                    onValueChange = { priority = it },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Novos Campos
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Recorrência") },
                    value = recurrence,
                    onValueChange = { recurrence = it },
                    shape = MaterialTheme.shapes.medium,
                    placeholder = { Text("Ex: Diário, Semanal") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Data limite") },
                    value = deadline,
                    onValueChange = { deadline = it },
                    shape = MaterialTheme.shapes.medium,
                    placeholder = { Text("DD/MM/AAAA") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Local") },
                        value = locationName,
                        onValueChange = { locationName = it },
                        shape = MaterialTheme.shapes.medium,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Abrir mapa",
                                modifier = Modifier.clickable {
                                    showMapPicker = true
                                }
                            )
                        }
                    )
                    Button(
                        onClick = { showMapPicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Selecionar no mapa")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onConfirm(
                            name,
                            category,
                            reminderTime,
                            priority,
                            recurrence,
                            deadline,
                            locationName,
                            latitude,
                            longitude
                        )
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Salvar meta")
                }
            }
        }
    }
}
