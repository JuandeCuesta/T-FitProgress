package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class Entrenamiento : Serializable {

    var id:String = ""
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

    var descripcion:String = ""
        get() = field
        set(value) {
            field = value
        }

    var series:Int = 0
        get() = field
        set(value) {
            field = value
        }
    var repeticiones:Int = 0
        get() = field
        set(value) {
            field = value
        }
    var descanso:Int = 0
        get() = field
        set(value) {
            field = value
        }

    var ejercicios: MutableList<String> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

}