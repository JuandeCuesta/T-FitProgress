package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat

class Entrenamiento_Deportista: Serializable {

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