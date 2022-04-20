package edu.juandecuesta.t_fitprogress.model

class Entrenamiento {

    var codigoEntrenamiento:String = ""
        get() = field
        set(value) {
            field = value
        }
    var nombre:String = ""
        get() = field
        set(value) {
            field = value
        }

    var tipo:String = ""
        get() = field
        set(value) {
            field = value
        }

    var ejercicios: MutableList<Ejercicio>?=null
        get() = field
        set(value) {
            field = value
        }

    init {

    }
}