package com.conkers.mapas.api

import android.util.Log
import retrofit2.HttpException

data class RouteResponse(
    val routes: List<RouteFeature>
)

data class RouteFeature(
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<Double>>,
    val type: String
)

data class GeoJsonRequest(
    val format_in: String = "geojson", // Formato de entrada
    val format_out: String = "geojson", // Formato de salida
    val geometry: Geometry // Incluye el modelo Geometry
)

data class GeoJsonResponse(
    val geometry: Geometry,
    val elevation: Double // Ejemplo: añade el dato que esperas en la respuesta
)

suspend fun fetchRoutePost(
    api: DirectionsApi,
    apiKey: String,
    coordinates: List<List<Double>>
): RouteResponse? {
    return try {
        val requestBody = GeoJsonRequest(
            geometry = Geometry(
                coordinates = coordinates,
                type = "LineString"
            )
        )
        Log.d("Request Body", "Cuerpo enviado: $requestBody")
        api.postRoute(apiKey = "Bearer $apiKey", body = requestBody)
    } catch (e: HttpException) {
        Log.e("HTTP Error", "Error HTTP: ${e.code()} - ${e.message()}")
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
