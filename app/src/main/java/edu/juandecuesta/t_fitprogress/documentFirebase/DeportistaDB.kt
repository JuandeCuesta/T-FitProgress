package edu.juandecuesta.t_fitprogress.documentFirebase

import edu.juandecuesta.t_fitprogress.model.CondicionFisica
import edu.juandecuesta.t_fitprogress.model.Entrenador
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import java.io.Serializable

class DeportistaDB:Serializable {
    var apellido:String = ""
    var nombre:String = ""
    var soyEntrenador:Boolean = false
    var entrenador: String= ""
    var fechanacimiento:String = ""
    var experiencia:String = ""
    var evoluacionFisica: MutableList<CondicionFisica>?=null
    var entrenamientos: MutableList<Entrenamiento_Deportista>?=null
    var sexo:String = ""


}