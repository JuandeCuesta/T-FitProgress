package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.utils.Functions

class HomeViewModel : ViewModel() {

    var entrenamientos: MutableList<Entrenamiento_Deportista> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

}