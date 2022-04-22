package edu.juandecuesta.t_fitprogress.model

class Entrenador:Usuario() {
    //Almacenar los ids
    var deportistas: MutableList<Deportista> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }
    var ejercicios: MutableList<Ejercicio> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }
    var entrenamientos: MutableList<Entrenamiento> = arrayListOf()
        get() = field
        set(value) {
            field = value
        }
}