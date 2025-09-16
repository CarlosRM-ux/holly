package com.example.holly.Domain.model.rasgosPersonalidad

data class CompatibilityScore(
    val totalScore: Double,
    val relationshipAffinityScore: Double,
    val personalityScore: Double,
    val interestScore: Double,
    val valueScore: Double,
    val locationScore: Double,
    val qualityScore: Double,
    val explanation: String
)
