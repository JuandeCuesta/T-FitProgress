package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class Evaluacion_Deportista: Serializable {
    var id = ""
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