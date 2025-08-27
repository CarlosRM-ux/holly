package com.example.holly.Data.repository

import com.example.holly.Domain.model.Photo


data class UsuarioMatch(
    val photos: List<Photo>, // Ahora guarda una lista de objetos Photo
    val nombre: String
)