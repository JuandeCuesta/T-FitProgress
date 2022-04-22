package edu.juandecuesta.t_fitprogress.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.model.Entrenador
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import java.util.*
import kotlin.collections.ArrayList

class Functions {

    fun mostrarFecha(): String {
        val hoy = Calendar.getInstance()
        return "${hoy.get(Calendar.DAY_OF_MONTH)}" +
                "/${hoy.get(Calendar.MONTH) + 1}" +
                "/${hoy.get(Calendar.YEAR)}"
    }

    fun loadEntrenador (db:FirebaseFirestore,email:String,doc: DocumentSnapshot):Entrenador{

        val entrenador = Entrenador()

        entrenador.nombre = doc.get("nombre") as String
        entrenador.apellido = doc.get("apellido") as String
        entrenador.soyEntrenador = doc.get("soyEntrenador") as Boolean

        if (doc.get("deportistas") != null){
            /*val emailsDeportista = doc.get("deportistas") as MutableList<String>
            val deportistas:MutableList<Deportista> = arrayListOf()

            db.collection("users").get().addOnSuccessListener {
                for (doc in it){
                    emailsDeportista.forEach { e -> if (e.equals(doc.id)){
                        val deportista = Deportista()

                    }}
                }
            }

            for (emailDep in emailsDeportista){

            }*/

            entrenador.deportistas = doc.get("deportistas") as MutableList<Deportista>
        }

        if (doc.get("ejercicios") != null){
            entrenador.ejercicios = doc.get("ejercicios") as MutableList<Ejercicio>
        }

        if (doc.get("entrenamientos") != null){
            entrenador.entrenamientos = doc.get("entrenamientos") as MutableList<Entrenamiento>
        }

        return entrenador
    }
}