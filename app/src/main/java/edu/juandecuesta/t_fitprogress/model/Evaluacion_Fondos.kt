package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Evaluacion_Fondos:Serializable {

    var fechaFormat: Date?=null

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

    var fondos:Int = 0
        get() = field
        set(value) {
            field = value
        }


}