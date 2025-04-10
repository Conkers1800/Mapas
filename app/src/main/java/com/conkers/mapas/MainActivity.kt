package com.conkers.mapas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.conkers.mapas.Screen.MapScreen

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadMap() // Cargar el mapa si el permiso es concedido
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el permiso ya está concedido
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadMap() // El permiso ya está otorgado, cargar el mapa
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Solicitar permiso
        }
    }

    private fun loadMap() {
        setContent {
            MapScreen(context = this) // Mostrar la pantalla del mapa
        }
    }
}
