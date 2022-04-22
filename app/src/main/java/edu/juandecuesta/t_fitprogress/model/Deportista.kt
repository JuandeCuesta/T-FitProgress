package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class Deportista:Usuario(){
    var entrenador:Entrenador?= null
        get() = field
        set(value) {
            field = value
        }
    var experiencia:String = ""
        get() = field
        set(value) {
            field = value
        }
    var sexo:String = ""
        get() = field
        set(value) {
            field = value
        }

    var edad:Int = 0
        get() = field
        set(value) {
            field = value
        }
    var altura:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var pesoInicial:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var evoluacionFisica: MutableList<CondicionFisica> = arrayListOf()
    var entrenamientos: MutableList<Entrenamiento_Deportista> = arrayListOf()
}