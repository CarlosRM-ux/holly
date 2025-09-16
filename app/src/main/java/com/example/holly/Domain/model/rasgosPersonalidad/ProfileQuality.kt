package com.example.holly.Domain.model.rasgosPersonalidad

data class ProfileQuality(
    val photoCount: Int,
    val hasCompleteDescription: Boolean,
    val profileCompleteness: Double, // 0.0 a 1.0
    val lastActiveHours: Int
)
