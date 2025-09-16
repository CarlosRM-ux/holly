package com.example.holly.Domain.model.rasgosPersonalidad

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val maxDistance: Double? = null // km
)
