package edu.juandecuesta.t_fitprogress.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
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


    fun calcularEdad (fecha:String):Int{
        val dia = fecha.split("/")[0].toInt()
        val mes = fecha.split("/")[1].toInt()
        val anyo = fecha.split("/")[2].toInt()
        val hoy = Calendar.getInstance()

        var anyoCal = hoy.get(Calendar.YEAR) - anyo
        val mesCal = (hoy.get(Calendar.MONTH) + 1) - mes
        val diaCal = hoy.get(Calendar.DAY_OF_MONTH)  - dia

        if (mesCal<0 || (mesCal == 0 && diaCal < 0)){
            anyoCal--
        }

        return anyoCal
    }

    fun showSnackSimple(vista: View, mensaje: String) {
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show()
    }

    fun buscarIdURL (url:String): String{
        val indice:Int = url.indexOf("?v=")
        return url.substring(indice).replace("?v=","")
    }
    fun buscarCodeImg (url:String): String{
        val indice:Int = url.indexOf("image%")

        if (indice != -1){
            return url.substring(indice).replace("image%","")
        }
        return (0..100000).random().toString()
    }

}