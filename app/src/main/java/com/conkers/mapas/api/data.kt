package com.conkers.mapas.api

import android.util.Log
import com.google.gson.Gson
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
        Log.d("Debug", "Cuerpo de la solicitud: ${
            Gson().toJson(GeoJsonRequest(
            geometry = Geometry(
                coordinates = coordinates,
                type = "LineString"
            )
        ))}")

        val requestBody = GeoJsonRequest(
            geometry = Geometry(
                coordinates = coordinates,
                type = "LineString"
            )
        )
        val response = api.postRoute(apiKey = "Bearer $apiKey", body = requestBody)
        response
    } catch (e: HttpException) {
        Log.e("HTTP Error", "Error HTTP: ${e.code()} - Detalles: ${e.response()?.errorBody()?.string()}")
        null
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("API Error", "Error al conectar con la API: ${e.message}")
        null
    }
}
