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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("La aplicación necesita acceso a tu ubicación para trazar rutas.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Conceder permiso")
        }
    }
}
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

fun checkLocationSettings(
    context: Context,
    onGPSAvailable: () -> Unit,
    onGPSUnavailable: () -> Unit
) {
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    val settingsClient = LocationServices.getSettingsClient(context)
    val task = settingsClient.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        Log.d("GPS", "Configuraciones de ubicación correctas")
        onGPSAvailable()
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                exception.startResolutionForResult(context as Activity, 100)
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.e("GPS Error", "Error al solicitar habilitación del GPS")
            }
        } else {
            Log.e("GPS", "El GPS no está disponible")
            onGPSUnavailable()
        }
    }
}


fun getCurrentLocation(context: Context, onSuccess: (Location) -> Unit, onError: () -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("Ubicación Actual", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                    onSuccess(location)
                } else {
                    Log.e("Ubicación Actual", "No se pudo obtener la ubicación")
                    onError()
                }
            }
            .addOnFailureListener {
                Log.e("Ubicación Error", "Error al obtener la ubicación")
                onError()
            }
    } catch (e: SecurityException) {
        Log.e("Permiso de Ubicación", "Permiso denegado o no configurado correctamente")
        e.printStackTrace()
        onError()
    }
}
