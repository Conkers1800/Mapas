package com.conkers.mapas.Screen

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(routeCoordinates: List<List<Double>>) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    mapView.setTileSource(TileSourceFactory.MAPNIK)
    mapView.controller.setZoom(15.0)
    mapView.controller.setCenter(GeoPoint(routeCoordinates[0][1], routeCoordinates[0][0]))

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )

    drawRoute(mapView, routeCoordinates)
}

fun drawRoute(mapView: MapView, routeCoordinates: List<List<Double>>) {
    val geoPoints = routeCoordinates.map { GeoPoint(it[1], it[0]) } // [long, lat]
    val roadOverlay = Polyline()
    roadOverlay.setPoints(geoPoints)
    mapView.overlayManager.add(roadOverlay)
    mapView.invalidate()
}


