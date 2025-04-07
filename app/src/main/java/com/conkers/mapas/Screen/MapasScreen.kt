package com.conkers.mapas.Screen

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(context: Context) {
    val mapView = remember { MapView(context) }
    Configuration.getInstance().userAgentValue = context.packageName

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )

    val startPoint = GeoPoint(20.1234, -101.1234) // Ejemplo
    val endPoint = GeoPoint(20.4567, -101.4567) // Configurable dirección

    drawRoute(mapView, listOf(startPoint, endPoint))
}

fun drawRoute(mapView: MapView, routeCoordinates: List<GeoPoint>) {
    val roadOverlay = Polyline()
    roadOverlay.setPoints(routeCoordinates)
    mapView.overlayManager.add(roadOverlay)
    mapView.invalidate()
}
