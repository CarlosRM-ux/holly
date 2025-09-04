package com.example.holly.Domain.model
 //Conceptos de Varianza:
//out (productor): La clase devuelve valores. Es segura para la herencia.
//
//Sin out (consumidor): La clase acepta valores. El tipo tiene que ser exacto.

sealed class Result <out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}