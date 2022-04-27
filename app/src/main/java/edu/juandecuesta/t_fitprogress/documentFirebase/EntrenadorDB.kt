package edu.juandecuesta.t_fitprogress.documentFirebase

import java.io.Serializable

class EntrenadorDB:Serializable {
    var apellido:String = ""
    var nombre:String = ""
    var email:String = ""
    var soyEntrenador:Boolean = true
    var deportistas:MutableList<String> = arrayListOf()
    var ejercicios:MutableList<String> = arrayListOf()
    var entrenamientos:MutableList<String> = arrayListOf()
}