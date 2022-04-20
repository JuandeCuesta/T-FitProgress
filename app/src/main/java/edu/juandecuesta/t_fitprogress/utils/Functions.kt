package edu.juandecuesta.t_fitprogress.utils

import java.util.*

class Functions {

    fun mostrarFecha(): String {
        val hoy = Calendar.getInstance()
        return "${hoy.get(Calendar.DAY_OF_MONTH)}" +
                "/${hoy.get(Calendar.MONTH) + 1}" +
                "/${hoy.get(Calendar.YEAR)}"
    }
}