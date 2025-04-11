package com.conkers.mapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.conkers.mapas.Screen.MapScreen


import androidx.compose.runtime.*
import com.conkers.mapas.otros.RequestPermissionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var permissionsGranted by remember { mutableStateOf(false) }

            if (permissionsGranted) {
                // Si los permisos son concedidos, mostrar el mapa
                MapScreen(context = this)
            } else {
                // Mostrar la pantalla de solicitud de permisos
                RequestPermissionsScreen(
                    onPermissionsGranted = {
                        permissionsGranted = true
                    }
                )
            }
        }
    }
}
