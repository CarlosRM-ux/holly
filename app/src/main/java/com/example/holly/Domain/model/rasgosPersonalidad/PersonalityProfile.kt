package com.example.holly.Domain.model.rasgosPersonalidad

data class PersonalityProfile(
    val  traits : Map<PersonalityTrait, Double> // Puntajes de 0.0 a 1.0
)
