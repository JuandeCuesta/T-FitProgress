package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable

open class Usuario: Serializable {
    var nombre:String = ""
        get() = field
        set(value) {
            field = value
        }
    var apellido:String = ""
        get() = field
        set(value) {
            field = value
        }
    var soyEntrenador:Boolean = true
        get() = field
        set(value) {
            field = value
        }

}