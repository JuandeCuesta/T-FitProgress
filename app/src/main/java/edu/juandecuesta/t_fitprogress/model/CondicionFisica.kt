package edu.juandecuesta.t_fitprogress.model

import java.io.Serializable
import java.text.SimpleDateFormat

class CondicionFisica:Serializable {
    var fecha:SimpleDateFormat? = null
        get() = field
        set(value) {
            field = value
        }
    var peso:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var mGrasa:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var mMagra:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var cAnaerobica:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var cAerobica:Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    init {

    }
}