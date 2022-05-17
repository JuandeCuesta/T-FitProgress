package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Evaluacion_Imc:Serializable {


    var altura:Float = 0.0f

    var fechaFormat: Date?=null

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

    var peso:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var resultado:String = ""

    var imc:Float = 0.0f

}