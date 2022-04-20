package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.utils.Functions

class HomeViewModel : ViewModel() {

    var fecha: String = Functions().mostrarFecha()

    var deportistas: MutableList<Deportista>?=null
        get() = field
        set(value) {
            field = MainActivity.entrenador.deportistas
        }

}