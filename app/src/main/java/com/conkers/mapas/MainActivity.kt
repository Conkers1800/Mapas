package com.conkers.mapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.conkers.mapas.Screen.MapScreen
import com.conkers.mapas.otros.PermissionScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var permissionGranted by remember { mutableStateOf(false) }

            if (permissionGranted) {
                MapScreen(context = this)
            } else {
                PermissionScreen(onPermissionGranted = { permissionGranted = true })
            }
        }
    }
}
