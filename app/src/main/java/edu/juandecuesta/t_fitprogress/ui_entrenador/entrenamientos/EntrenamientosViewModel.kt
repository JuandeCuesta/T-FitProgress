package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.model.Entrenamiento

class EntrenamientosViewModel:ViewModel() {
    var entrenamientos: MutableList<Entrenamiento> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }
}