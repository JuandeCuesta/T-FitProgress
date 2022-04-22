package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

class Ejercicio: Serializable {

    var codigoEjercicio:String = ""
        get() = field
        set(value) {
            field = value
        }
    var nombre:String = ""
        get() = field
        set(value) {
            field = value
        }
    var tipo:String = ""
        get() = field
        set(value) {
            field = value
        }
    var categoria:String = ""
        get() = field
        set(value) {
            field = value
        }
    var descripcion:String = ""
        get() = field
        set(value) {
            field = value
        }
    var urlImagen:String = ""
        get() = field
        set(value) {
            field = value
        }
    var urlVideo:String = ""
        get() = field
        set(value) {
            field = value
        }

    init {

    }
}