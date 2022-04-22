package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class Entrenamiento : Serializable {

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

    var ejercicios: MutableList<Ejercicio> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

    init {

    }
}