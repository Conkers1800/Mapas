package com.conkers.mapas.Screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.conkers.mapas.api.MapasViewModel
import kotlinx.coroutines.*
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

@Composable
fun MapScreen(context: Context, mapViewModel: MapasViewModel= viewModel()) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var destinationInput by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(factory = {
            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            val map = MapView(context)
            map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            map.setBuiltInZoomControls(true)
            map.setMultiTouchControls(true)
            map.controller.setZoom(15.0)
            mapViewModel.setMapView(map, context)
            map
        }, update = {}, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = { isMenuOpen = !isMenuOpen },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú")
        }
        if (isMenuOpen) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .fillMaxWidth(0.9f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = destinationInput,
                        onValueChange = { destinationInput = it },
                        label = { Text("Ingresar destino") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                isMenuOpen = false
                                mapViewModel.searchAndRouteTo(context, destinationInput)
                            },
                            modifier = Modifier.weight(1f)
                                .padding(end = 8.dp) // Espaciado entre botones
                        ) {
                            Text("Buscar Ruta")
                        }

                        Button(
                            onClick = {
                                mapViewModel.showCurrentLocation(context)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ubicación actual")
                        }
                    }
                }
            }
        }
    }
}