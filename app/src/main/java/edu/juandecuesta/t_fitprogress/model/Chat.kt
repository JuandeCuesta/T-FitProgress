package edu.juandecuesta.t_fitprogress.model

import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB

class Chat {
    var deportista:DeportistaDB = DeportistaDB()
    var mensajes:MutableList<Mensaje> = arrayListOf()
}