package edu.juandecuesta.t_fitprogress.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import java.text.SimpleDateFormat
import java.util.*

class Functions {

    fun mostrarFecha(): String {
        val hoy = Calendar.getInstance()
        var dia = ""
        var mes = ""

        if ((hoy.get(Calendar.DAY_OF_MONTH)) < 10){
            dia = "0${hoy.get(Calendar.DAY_OF_MONTH)}"
        }else {
            dia = "${hoy.get(Calendar.DAY_OF_MONTH)}"
        }

        if ((hoy.get(Calendar.MONTH) + 1) < 10){
            mes = "0${hoy.get(Calendar.MONTH) + 1}"
        }else {
            mes = "${hoy.get(Calendar.MONTH) + 1}"
        }


        return dia + "/$mes" +"/${hoy.get(Calendar.YEAR)}"
    }

    fun calcularFecha (f:String):Int{
        val hoy = mostrarFecha()
        val fecha: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate:Date = sdf.parse(hoy)
        fecha.time = currentDate
        val currentDateEnMillis = fecha.timeInMillis

        var fecha2: Calendar = Calendar.getInstance()
        val date:Date = sdf.parse(f)
        fecha2.time = date
        val dateEnMillis = fecha2.timeInMillis

        return currentDateEnMillis.compareTo(dateEnMillis)
    }

    fun diaSemana (f:String, context:Context):String{
        val fecha: Calendar = Calendar.getInstance()
        val dia = f.split("/")[0].toInt()
        val mes = f.split("/")[1].toInt()
        val anyo = f.split("/")[2].toInt()
        fecha.set(anyo,(mes-1), dia)
        val diasemana = fecha.get(Calendar.DAY_OF_WEEK)
        val array = context.resources.getStringArray(R.array.diasSemana)
        val fSemana = "${array[diasemana-1]} - $f"
        return fSemana
    }

    fun  ultimoDiaSemana():String{
        val fecha: Calendar = Calendar.getInstance()
        val f = mostrarFecha()
        val dia = f.split("/")[0].toInt()
        val mes = f.split("/")[1].toInt()
        val anyo = f.split("/")[2].toInt()
        fecha.set(anyo,(mes-1), dia)

        val dayWeek = fecha.get(Calendar.DAY_OF_WEEK)

        if (dayWeek == 1){
            fecha.add(Calendar.DAY_OF_MONTH,-1)
        }
        val day = fecha.get(Calendar.DAY_OF_WEEK)
        fecha.firstDayOfWeek = Calendar.MONDAY
        fecha.add(Calendar.DATE, fecha.firstDayOfWeek - day)
        fecha.add(Calendar.DATE, 6)


        return "${fecha.get(Calendar.DAY_OF_MONTH)}" + "/${fecha.get(Calendar.MONTH) + 1}" +"/${fecha.get(Calendar.YEAR)}"

    }

    fun calcularEntreFechas (f1:String,f2:String):Int{
        val fecha: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate:Date = sdf.parse(f1)
        fecha.time = currentDate
        val currentDateEnMillis = fecha.timeInMillis

        var fecha2: Calendar = Calendar.getInstance()
        val date:Date = sdf.parse(f2)
        fecha2.time = date
        val dateEnMillis = fecha2.timeInMillis

        return currentDateEnMillis.compareTo(dateEnMillis)
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