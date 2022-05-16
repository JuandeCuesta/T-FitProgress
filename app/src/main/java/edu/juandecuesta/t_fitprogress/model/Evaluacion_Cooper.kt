package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Evaluacion_Cooper:Serializable {


    var fechaFormat: Date?=null

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }


    var distancia:Int = 0
    var vo2_max: Float = 0.0f  //VO2 Max = 22,351 x Distancia recorrida (en kilómetros) – 11,288

}