package edu.juandecuesta.t_fitprogress.documentFirebase

import java.io.Serializable
import java.text.SimpleDateFormat

class Entrenamiento_DeportistaDB: Serializable {

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

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