package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Entrenamiento_Deportista: Serializable {

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

    var fechaFormat:Date?=null

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
}