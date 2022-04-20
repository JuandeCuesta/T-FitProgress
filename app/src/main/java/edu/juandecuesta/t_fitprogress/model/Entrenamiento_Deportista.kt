package edu.juandecuesta.t_fitprogress.model

import java.text.SimpleDateFormat

class Entrenamiento_Deportista {

    var fecha: SimpleDateFormat? = null
        get() = field
        set(value) {
            field = value
        }

    var entrenamiento:Entrenamiento?=null
        get() = field
        set(value) {
            field = value
        }

    var realizado:Boolean = false
        get() = field
        set(value) {
            field = value
        }
    init {

    }
}