package edu.juandecuesta.t_fitprogress.documentFirebase

import edu.juandecuesta.t_fitprogress.model.CondicionFisica
import edu.juandecuesta.t_fitprogress.model.Entrenador
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista

class deportistaDB {
    var apellido:String = ""
    var nombre:String = ""
    var soyEntrenador:Boolean = false
    var entrenador: String= ""
    var experiencia:String = ""
    var edad:Int = 0
    var altura:Float = 0.0f
    var pesoInicial:Float = 0.0f
    var evoluacionFisica: MutableList<CondicionFisica>?=null
    var entrenamientos: MutableList<Entrenamiento_Deportista>?=null
    var sexo:String = ""
}