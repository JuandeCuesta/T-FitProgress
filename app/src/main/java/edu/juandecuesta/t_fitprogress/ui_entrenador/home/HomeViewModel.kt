package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista

class HomeViewModel : ViewModel() {

    var entrenamientos: MutableList<Entrenamiento_Deportista> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

}