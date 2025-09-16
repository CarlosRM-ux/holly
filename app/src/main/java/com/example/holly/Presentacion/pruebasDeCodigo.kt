package com.example.holly.Presentacion

class pruebasDeCodigo {

    sealed class Animal {
        data class Perro(val nombre: String) : Animal()
        data class Gato(val nombre: String) : Animal()
    }


    fun main() {
        fun imprimir(animal: Animal) {
            when (animal) {
                is Animal.Perro -> println("Es un perro llamado ${animal.nombre}")
                is Animal.Gato -> println("Es un gato llamado ${animal.nombre}")
            }
        }
        imprimir(Animal.Perro("fulanito"))
    }

}