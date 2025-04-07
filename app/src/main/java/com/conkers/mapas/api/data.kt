package com.conkers.mapas.api

data class RouteResponse(
    val features: List<Feature>
)

data class Feature(
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<Double>>
)

suspend fun fetchRoute(
    api: DirectionsApi,
    start: String,
    end: String,
    apiKey: String
): List<List<Double>> {
    val response = api.getRoute(start = start, end = end, apiKey = apiKey)
    return response.features.first().geometry.coordinates
}
