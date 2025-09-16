package com.example.holly.Domain.model.rasgosPersonalidad

data class InterestProfile(
    val interests: Map<InterestCategory, List<String>>,
    val weights: Map<InterestCategory, Double> // Importancia de cada categoría
)
