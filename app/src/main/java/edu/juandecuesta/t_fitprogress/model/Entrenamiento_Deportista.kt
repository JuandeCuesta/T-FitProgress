package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.util.*

class Entrenamiento_Deportista: Serializable {

    var posicion:Int = 0
    var prueba:String = ""

    var deportista: DeportistaDB = DeportistaDB()

    var fecha: String = ""
        get() = field
        set(value) {
            field = value
        }

    var fechaFormat:Date?=null

    var primerodeldia:Boolean = false

    var entrenamiento:Entrenamiento=Entrenamiento()
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