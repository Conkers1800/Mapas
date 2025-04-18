package com.conkers.mapas.api

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.conkers.mapas.Screen.ServicioLocalizacion
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapasViewModel : ViewModel() {

    private val _routePoints = MutableLiveData<List<GeoPoint>>()
    val routePoints: LiveData<List<GeoPoint>> = _routePoints
    private val client = OkHttpClient()
    private var mapView: MapView? = null
    private var currentLocation: GeoPoint? = null
    private var currentDestination: GeoPoint? = null

    fun setMapView(map: MapView, context: Context) {
        this.mapView = map
        showCurrentLocation(context) // Mostrar ubicación actual en el mapa
    }
    fun showCurrentLocation(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val servicioLocalizacion = ServicioLocalizacion(context)
            val location = servicioLocalizacion.getCurrentLocation()

            val currentGeoPoint = if (location != null) {
                GeoPoint(location.latitude, location.longitude)
            } else {
                GeoPoint(20.427025, -101.167665)
            }

            currentLocation = currentGeoPoint

            withContext(Dispatchers.Main) {
                addCurrentLocationMarker(currentGeoPoint) // Añadir el marcador
                addCircleOnMap(currentGeoPoint, 10.0)
            }
        }
    }
    private fun addCircleOnMap(center: GeoPoint, radiusInMeters: Double) {
        mapView?.let { map ->
            // Crear un objeto Polygon para representar el círculo
            val circle = org.osmdroid.views.overlay.Polygon(map).apply {
                points = Polygon.pointsAsCircle(center, radiusInMeters)
                fillColor = android.graphics.Color.argb(75, 0, 0, 255) //Circulo transparente
                strokeColor = android.graphics.Color.BLUE // Azul sólido para el borde del círculo
                strokeWidth = 2f // Ancho del borde
            }
            map.overlays.add(circle)
            map.invalidate() // Refrescar el mapa para mostrar los cambios
        }
    }
    private fun addCurrentLocationMarker(geoPoint: GeoPoint) {
        mapView?.let { map ->
            val marker = Marker(map).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Ubicación actual"
            }
            // Reemplazamos el ícono con un círculo dibujado en el mapa
            marker.icon = createDrawableCircle(map.context)
            // Limpiar marcador
            map.overlays.removeIf { it is Marker && it.title == "Ubicación actual" }
            map.overlays.add(marker)

            // Centrar el mapa en la ubicación actual
            map.controller.setCenter(geoPoint)
            map.invalidate()
        }
    }

    private fun createDrawableCircle(context: Context): android.graphics.drawable.Drawable {
        // Tamaño del círculo
        val circleRadius = 50 // En píxeles

        // Crear un Bitmap y un Canvas para dibujar el círculo
        val bitmap = android.graphics.Bitmap.createBitmap(circleRadius * 2, circleRadius * 2, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Configurar el Paint para dibujar el círculo
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLUE
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }

        // Dibujar el círculo
        canvas.drawCircle(circleRadius.toFloat(), circleRadius.toFloat(), circleRadius.toFloat(), paint)
        return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
    }


    fun searchAndRouteTo(context: Context, address: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val results = geocoder.getFromLocationName(address, 1)
                if (!results.isNullOrEmpty()) {
                    val destination = GeoPoint(results[0].latitude, results[0].longitude)
                    currentLocation?.let { start ->
                        fetchRoute(start, destination)
                    }
                } else {
                    Log.e("GEOCODER", "Dirección no encontrada: $address")
                }
            } catch (e: Exception) {
                Log.e("GEOCODER", "Error en Geocoder: ${e.message}")
            }
        }
    }

    private fun fetchRoute(start: GeoPoint, end: GeoPoint) {
        val url =
            "https://api.openrouteservice.org/v2/directions/driving-car?start=" +
                    "${start.longitude}," +
                    "${start.latitude}" +
                    "&end=${end.longitude}," +
                    "${end.latitude}"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization",
                        "5b3ce3597851110001cf62483410b16241c145f69948237a5fc678f6")
                    .build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                val json = JSONObject(responseBody)
                if (!json.has("features")) {
                    Log.e("API", "Respuesta inválida'): $responseBody")
                    return@launch
                }
                val coordinates = json
                    .getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates")

                val points = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    points.add(GeoPoint(coord.getDouble(1), coord.getDouble(0)))
                }
                withContext(Dispatchers.Main) {
                    _routePoints.value = points
                    drawRoute()
                }

            } catch (e: Exception) {
                Log.e("API", "Error de ruta: ${e.message}")
            }
        }
    }
    fun drawRoute() {
        val polyline = Polyline().apply {
            routePoints.value?.let { setPoints(it) }
            outlinePaint.strokeWidth = 8f
        }
        mapView?.overlays?.clear()
        mapView?.overlays?.add(polyline)
        mapView?.invalidate()
    }
}