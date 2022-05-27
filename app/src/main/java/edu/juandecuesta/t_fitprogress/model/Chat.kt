package edu.juandecuesta.t_fitprogress.model

class Chat {
    var deportista: DeportistaDB = DeportistaDB()
    var mensajes:MutableList<Mensaje> = arrayListOf()
}