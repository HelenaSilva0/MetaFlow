package com.example.metaflow.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (LatLng, String) -> Unit
) {
    val context = LocalContext.current
    val initialLocation = LatLng(-23.5505, -46.6333) // São Paulo
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Selecionar Local", style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (selectedLocation != null) 
                                "Local selecionado" 
                            else "Toque no mapa para marcar o local",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedLocation != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selectedLocation != null) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                selectedLocation?.let {
                                    onLocationSelected(it, "Local: ${String.format("%.4f", it.latitude)}, ${String.format("%.4f", it.longitude)}")
                                }
                            },
                            enabled = selectedLocation != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Text("Confirmar Local")
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.LightGray) // Fundo visível se o mapa falhar
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false,
                        compassEnabled = true
                    ),
                    onMapClick = {
                        selectedLocation = it
                    }
                ) {
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Local da Meta",
                            snippet = "Sua meta será aqui"
                        )
                    }
                }
                
                // Overlay de ajuda
                if (selectedLocation == null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            "Toque no mapa para escolher o ponto",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
