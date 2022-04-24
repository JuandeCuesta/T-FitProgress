package edu.juandecuesta.t_fitprogress.utils

import com.google.firebase.firestore.DocumentSnapshot
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import java.util.*

class Functions {

    fun mostrarFecha(): String {
        val hoy = Calendar.getInstance()
        return "${hoy.get(Calendar.DAY_OF_MONTH)}" +
                "/${hoy.get(Calendar.MONTH) + 1}" +
                "/${hoy.get(Calendar.YEAR)}"
    }

    fun loadEntrenador (doc: DocumentSnapshot): EntrenadorDB {

        val entrenadorDb = doc.toObject(EntrenadorDB::class.java)

        return entrenadorDb!!
    }
}