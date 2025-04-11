package com.conkers.mapas.otros

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

@Composable
fun RequestPermissionsScreen(onPermissionsGranted: () -> Unit) {
    val context = LocalContext.current
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                Log.d("Permisos", "Permiso de ubicación precisa otorgado")
                onPermissionsGranted()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                Log.d("Permisos", "Permiso de ubicación aproximada otorgado")
                onPermissionsGranted()
            }
            else -> {
                Toast.makeText(context, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Por favor, otorga permisos de ubicación para continuar.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }) {
            Text("Solicitar permisos")
        }
    }
}