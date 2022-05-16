package edu.juandecuesta.t_fitprogress.documentFirebase

import edu.juandecuesta.t_fitprogress.model.Evaluacion_Cooper
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Deportista
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Fondos
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Imc
import java.io.Serializable

class DeportistaDB:Serializable {
    var apellido:String = ""
    var nombre:String = ""
    var email:String = ""
    var soyEntrenador:Boolean = false
    var entrenador: String= ""
    var fechanacimiento:String = ""
    var experiencia:String = ""
    var evaluacionfisica: MutableList<Evaluacion_Deportista>?=null
    var entrenamientos: MutableList<Entrenamiento_DeportistaDB>?=null
    var sexo:String = ""
    var descripcionPersonal = ""
    var objetivo = ""
    var fechacreacion = ""
    var urlImagen = ""
    var deshabilitada = false

}