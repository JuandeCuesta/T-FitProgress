package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat

class Entrenamiento_DeportistaDB: Serializable {

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

    var prueba:String = ""

    var entrenamiento:String=""
        get() = field
        set(value) {
            field = value
        }

    var realizado:Boolean = false
        get() = field
        set(value) {
            field = value
        }
}