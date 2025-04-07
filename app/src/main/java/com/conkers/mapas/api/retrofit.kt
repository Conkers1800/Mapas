package com.conkers.mapas.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("v2/directions/driving-car")
    suspend fun getRoute(
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("api_key") apiKey: String
    ): RouteResponse
}

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.openrouteservice.org/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api: DirectionsApi = retrofit.create(DirectionsApi::class.java)
