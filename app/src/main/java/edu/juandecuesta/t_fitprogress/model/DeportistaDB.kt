package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class DeportistaDB:Serializable {
    var apellido:String = ""
    var nombre:String = ""
    var email:String = ""
    var soyEntrenador:Boolean = false
    var entrenador: String= ""
    var fechanacimiento:String = ""
    var experiencia:String = ""
    var evaluacionfisica: MutableList<String>?=null
    var entrenamientos: MutableList<Entrenamiento_DeportistaDB>?=null
    var sexo:String = ""
    var descripcionPersonal = ""
    var objetivo = ""
    var fechacreacion = ""
    var urlImagen = ""
    var deshabilitada = false

}