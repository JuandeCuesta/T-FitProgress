package edu.juandecuesta.t_fitprogress.model

class Deportista:Usuario() {
    var codigoDeportista:String = ""
        get() = field
        set(value) {
            field = value
        }
    var entrenador:Entrenador?=null
        get() = field
        set(value) {
            field = value
        }

    var experiencia:String = ""
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
    var evoluacionFisica: MutableList<CondicionFisica>?=null
        get() = field
        set(value) {
            field = value
        }
    var entrenamientos: MutableList<Entrenamiento_Deportista>?=null
        get() = field
        set(value) {
            field = value
        }
    init {

    }
}