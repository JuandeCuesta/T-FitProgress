package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

import androidx.lifecycle.ViewModel
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB

class ClientesViewModel : ViewModel() {

    var deportistas: MutableList<DeportistaDB> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }

}