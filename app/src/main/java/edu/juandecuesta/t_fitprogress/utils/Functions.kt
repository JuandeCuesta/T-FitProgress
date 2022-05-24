package edu.juandecuesta.t_fitprogress.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.snackbar.Snackbar
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

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
        val array:Array<String> = arrayOf("Domingo", "Lunes", "Martes" ,"Miércoles", "Jueves", "Viernes", "Sábado")
        //val array = context.resources.getStringArray(R.array.diasSemana)
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

    fun calcularIMC(peso:Float, altura:Float):Float{
        return (peso / (altura / 100).pow(2))
    }

    fun resultadoIMC(imc: Float): String{
        val solution:Float = (Math.round(imc * 10.0) / 10.0).toFloat()
        val mensaje: String = when {
            solution < 18.5 -> "Bajo peso"
            solution in 18.5..24.9 -> "Normopeso"
            solution in 25.0..29.9 -> "Sobrepeso"
            solution in 30.0..34.9 -> "Obesidad grado I"
            solution in 35.0..39.9 -> "Obesidad grado II"
            else -> "Obesidad grado III"
        }

        return mensaje
    }
    fun calcularvo2max(distancia:Float):Float{
        return ((distancia - 504.9f)/44.73f)
    }

    fun resultadoCooper(distancia:Float, sexo:String, edad:Int):String{
        var mensaje: String = ""

        when {
            edad < 30 -> {
                when (sexo){
                    "Hombre" -> {
                        when {
                            distancia < 1600f -> mensaje = "Muy mala"
                            distancia in 1600f..2199f -> mensaje = "Mala"
                            distancia in 2200f..2399f -> mensaje = "Regular"
                            distancia in 2400f..2800f -> mensaje = "Buena"
                            distancia > 2800f -> mensaje = "Excelente"
                        }
                    }
                    "Mujer" -> {
                        when {
                            distancia < 1500f -> mensaje = "Muy mala"
                            distancia in 1500f..1799f -> mensaje = "Mala"
                            distancia in 1800f..2199f -> mensaje = "Regular"
                            distancia in 2200f..2700f -> mensaje = "Buena"
                            distancia > 2700f -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad in 30..39 -> {
                when (sexo){
                    "Hombre" -> {
                        when {
                            distancia < 1500f -> mensaje = "Muy mala"
                            distancia in 1500f..1899f -> mensaje = "Mala"
                            distancia in 1900f..2299f -> mensaje = "Regular"
                            distancia in 2300f..2700f -> mensaje = "Buena"
                            distancia > 2700f -> mensaje = "Excelente"
                        }
                    }
                    "Mujer" -> {
                        when {
                            distancia < 1400f -> mensaje = "Muy mala"
                            distancia in 1400f..1699f -> mensaje = "Mala"
                            distancia in 1700f..1999f -> mensaje = "Regular"
                            distancia in 2000f..2500f -> mensaje = "Buena"
                            distancia > 2500f -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad in 40..49 -> {
                when (sexo){
                    "Hombre" -> {
                        when {
                            distancia < 1400f -> mensaje = "Muy mala"
                            distancia in 1400f..1699f -> mensaje = "Mala"
                            distancia in 1700f..2099f -> mensaje = "Regular"
                            distancia in 2100f..2500f -> mensaje = "Buena"
                            distancia > 2500f -> mensaje = "Excelente"
                        }
                    }
                    "Mujer" -> {
                        when {
                            distancia < 1200f -> mensaje = "Muy mala"
                            distancia in 1200f..1499f -> mensaje = "Mala"
                            distancia in 1500f..1899f -> mensaje = "Regular"
                            distancia in 1900f..2300f -> mensaje = "Buena"
                            distancia > 2300f -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad > 49 -> {
                when (sexo){
                    "Hombre" -> {
                        when {
                            distancia < 1300f -> mensaje = "Muy mala"
                            distancia in 1300f..1599f -> mensaje = "Mala"
                            distancia in 1600f..1999f -> mensaje = "Regular"
                            distancia in 2000f..2400f -> mensaje = "Buena"
                            distancia > 2400f -> mensaje = "Excelente"
                        }
                    }
                    "Mujer" -> {
                        when {
                            distancia < 1100f -> mensaje = "Muy mala"
                            distancia in 1200f..1399f -> mensaje = "Mala"
                            distancia in 1400f..1699f -> mensaje = "Regular"
                            distancia in 1700f..2200f -> mensaje = "Buena"
                            distancia > 2200f -> mensaje = "Excelente"
                        }
                    }
                }
            }

        }
        return mensaje
    }

    fun formatearFecha (fecha: String):Date{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.parse(fecha)
    }

    fun formatearFechayHora (fecha: String):Date{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.parse(fecha)
    }

    fun resultadoFuerza(flexiones:Int, prueba:String, edad:Int):String{
        var mensaje: String = ""

        when {
            edad < 30 -> {
                when (prueba){
                    "Flexiones normales" -> {
                        when {
                            flexiones < 20 -> mensaje = "Mala"
                            flexiones in 20..33 -> mensaje = "Regular"
                            flexiones in 34..44 -> mensaje = "Promedio"
                            flexiones in 45..54 -> mensaje = "Buena"
                            flexiones > 54 -> mensaje = "Excelente"
                        }
                    }
                    "Flexiones modificadas" -> {
                        when {
                            flexiones < 6 -> mensaje = "Mala"
                            flexiones in 6..16 -> mensaje = "Regular"
                            flexiones in 17..33 -> mensaje = "Promedio"
                            flexiones in 34..38 -> mensaje = "Buena"
                            flexiones > 48 -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad in 30..39 -> {
                when (prueba){
                    "Flexiones normales" -> {
                        when {
                            flexiones < 15 -> mensaje = "Mala"
                            flexiones in 15..24 -> mensaje = "Regular"
                            flexiones in 25..34 -> mensaje = "Promedio"
                            flexiones in 35..44 -> mensaje = "Buena"
                            flexiones > 44 -> mensaje = "Excelente"
                        }
                    }
                    "Flexiones modificadas" -> {
                        when {
                            flexiones < 4 -> mensaje = "Mala"
                            flexiones in 4..11 -> mensaje = "Regular"
                            flexiones in 12..24 -> mensaje = "Promedio"
                            flexiones in 25..39 -> mensaje = "Buena"
                            flexiones > 39 -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad in 40..49 -> {
                when (prueba){
                    "Flexiones normales" -> {
                        when {
                            flexiones < 12 -> mensaje = "Mala"
                            flexiones in 12..19 -> mensaje = "Regular"
                            flexiones in 20..29 -> mensaje = "Promedio"
                            flexiones in 30..39 -> mensaje = "Buena"
                            flexiones > 39 -> mensaje = "Excelente"
                        }
                    }
                    "Flexiones modificadas" -> {
                        when {
                            flexiones < 3 -> mensaje = "Mala"
                            flexiones in 3..7 -> mensaje = "Regular"
                            flexiones in 8..19 -> mensaje = "Promedio"
                            flexiones in 20..34 -> mensaje = "Buena"
                            flexiones > 34 -> mensaje = "Excelente"
                        }
                    }
                }
            }
            edad in 50..59 -> {
                when (prueba){
                    "Flexiones normales" -> {
                        when {
                            flexiones < 8 -> mensaje = "Mala"
                            flexiones in 8..14 -> mensaje = "Regular"
                            flexiones in 15..34 -> mensaje = "Promedio"
                            flexiones in 25..34 -> mensaje = "Buena"
                            flexiones > 34 -> mensaje = "Excelente"
                        }
                    }
                    "Flexiones modificadas" -> {
                        when {
                            flexiones < 2 -> mensaje = "Mala"
                            flexiones in 2..5 -> mensaje = "Regular"
                            flexiones in 6..14 -> mensaje = "Promedio"
                            flexiones in 15..29 -> mensaje = "Buena"
                            flexiones > 29 -> mensaje = "Excelente"
                        }
                    }
                }
            }
            else -> {
                when (prueba){
                    "Flexiones normales" -> {
                        when {
                            flexiones < 5 -> mensaje = "Mala"
                            flexiones in 5..9 -> mensaje = "Regular"
                            flexiones in 10..19 -> mensaje = "Promedio"
                            flexiones in 20..29 -> mensaje = "Buena"
                            flexiones > 29 -> mensaje = "Excelente"
                        }
                    }
                    "Flexiones modificadas" -> {
                        when {
                            flexiones < 1 -> mensaje = "Mala"
                            flexiones in 1..2 -> mensaje = "Regular"
                            flexiones in 3..4 -> mensaje = "Promedio"
                            flexiones in 5..19 -> mensaje = "Buena"
                            flexiones > 19 -> mensaje = "Excelente"
                        }
                    }
                }
            }

        }
        return mensaje
    }

    fun obtenerMes(fecha: String,context: Context):String{
        val array = context.resources.getStringArray(R.array.mesesAbrev)
        val mes = fecha.split("/")[1].toInt()
        return array[mes-1]
    }

}