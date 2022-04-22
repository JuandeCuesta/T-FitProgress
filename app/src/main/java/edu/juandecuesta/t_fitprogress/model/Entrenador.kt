package edu.juandecuesta.t_fitprogress.model

class Entrenador:Usuario() {
    var deportistas: MutableList<Deportista>?=null
        get() = field
        set(value) {
            field = value
        }
    var ejercicios: MutableList<Ejercicio>?=null
        get() = field
        set(value) {
            field = value
        }
    var entrenamientos: MutableList<Entrenamiento>?=null
        get() = field
        set(value) {
            field = value
        }
}