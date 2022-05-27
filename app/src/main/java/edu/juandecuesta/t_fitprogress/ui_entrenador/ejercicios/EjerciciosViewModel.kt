package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.model.Ejercicio

class EjerciciosViewModel : ViewModel() {

    var ejercicios: MutableList<Ejercicio> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

}