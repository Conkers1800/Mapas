package com.conkers.mapas.Screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ServicioLocalizacion(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                    cont.resume(location)
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }
}
