package edu.juandecuesta.t_fitprogress.model

import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Evaluacion_Deportista: Serializable {

    var fecha = ""
    var evaluacionFondos = Evaluacion_Fondos()
    var evaluacionImc = Evaluacion_Imc()
    var evaluacionCooper = Evaluacion_Cooper()

    var realizado:Boolean = false
        get() = field
        set(value) {
            field = value
        }
}