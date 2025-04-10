package com.conkers.mapas.Screen

import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

import org.osmdroid.views.overlay.Polyline

@Composable
fun LocationInputScreen(
    onDismissRequest: () -> Unit,
    onRouteSubmitted: (String) -> Unit
) {
    var destination by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Ingresar dirección de destino") },
        text = {
            Column {
                TextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Dirección de destino (longitud,latitud)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (destination.matches(Regex("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?"))) {
                    onRouteSubmitted(destination)
                } else {
                    Toast.makeText(context, "Por favor ingresa coordenadas válidas", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
