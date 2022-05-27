package edu.juandecuesta.t_fitprogress.ui_entrenador.pruebas_fisicas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.esentrenador
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.*
import edu.juandecuesta.t_fitprogress.utils.Functions

class ShowPruebasActivity : AppCompatActivity() {
    private lateinit var binding:FragmentEvaluacionBinding
    var entrenamiento = Entrenamiento_Deportista()
    var evaluacionDeportista = Evaluacion_Deportista()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEvaluacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento_Deportista
        binding.txtFechaPrueba.text = entrenamiento.fecha
        val index = entrenamiento.fecha.lastIndexOf(" ")
        if (index > -1){
            entrenamiento.fecha = entrenamiento.fecha.substring((index+1))
        }


        if (entrenamiento.deportista.sexo == "Hombre") binding.etTipoFlexion.setText("Flexiones normales") else binding.etTipoFlexion.setText("Flexiones modificadas")
        cargar()

        if (esentrenador){
            val nombreCompleto = "${entrenamiento.deportista.nombre} ${entrenamiento.deportista.apellido}"
            binding.txtDeportista.text = nombreCompleto
            binding.txtDeportista.isVisible = true
            binding.etAltura.isFocusableInTouchMode = false
            binding.etPeso.isFocusableInTouchMode = false
            binding.etPeso.clearFocus()
            binding.etAltura.clearFocus()
            binding.etCooper.isFocusableInTouchMode = false
            binding.etCooper.clearFocus()
            binding.etFondos.isFocusableInTouchMode = false
            binding.etFondos.clearFocus()
            binding.btnCalcularIMC.isVisible = false
            binding.btnCalcularCooper.isVisible = false
            binding.btnCalcularFlexiones.isVisible = false
        }

        binding.TestIMCMore.setOnClickListener {
            binding.TestIMCMore.isVisible = false
            binding.lyIMCMore.isVisible = true
        }

        binding.TestIMCLess.setOnClickListener {
            binding.TestIMCMore.isVisible = true
            binding.lyIMCMore.isVisible = false
        }

        binding.TestFuerzaMore.setOnClickListener {
            binding.TestFuerzaMore.isVisible = false
            binding.lyFuerzaMore.isVisible = true
        }

        binding.TestFuerzaLess.setOnClickListener {
            binding.TestFuerzaMore.isVisible = true
            binding.lyFuerzaMore.isVisible = false
        }

        binding.TestCooperMore.setOnClickListener {
            binding.TestCooperMore.isVisible = false
            binding.lyCooperMore.isVisible = true
        }

        binding.TestCooperLess.setOnClickListener {
            binding.TestCooperMore.isVisible = true
            binding.lyCooperMore.isVisible = false
        }

        binding.btnCalcularIMC.setOnClickListener {
            if (comprobarcamposimc()){
                val peso = binding.etPeso.text.toString().toFloat()
                val altura = binding.etAltura.text.toString().toFloat()
                val imc = Functions().calcularIMC(peso, altura)
                val resultado = Functions().resultadoIMC(imc)
                val evaluacionImc = Evaluacion_Imc()
                evaluacionImc.peso = peso
                evaluacionImc.altura = altura
                evaluacionImc.imc = imc
                evaluacionImc.resultado = resultado
                evaluacionImc.fecha = evaluacionDeportista.fecha
                evaluacionImc.fechaFormat = Functions().formatearFecha(evaluacionImc.fecha)
                db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionImc", evaluacionImc).addOnSuccessListener {
                    mostrarimc(evaluacionImc)
                    evaluacionDeportista.evaluacionImc = evaluacionImc
                    actualizarestadoprueba()
                }
            }
        }

        binding.btnCalcularFlexiones.setOnClickListener {
            if (comprobarcamposflexiones()){
                val flexiones = binding.etFondos.text.toString().toInt()
                val prueba = binding.etTipoFlexion.text.toString()
                val edad = Functions().calcularEdad(entrenamiento.deportista.fechanacimiento)
                val resultado = Functions().resultadoFuerza(flexiones,prueba, edad)
                val evaluacionFondos = Evaluacion_Fondos()
                evaluacionFondos.fondos = flexiones
                evaluacionFondos.fecha = evaluacionDeportista.fecha
                evaluacionFondos.fechaFormat = Functions().formatearFecha(evaluacionFondos.fecha)
                evaluacionFondos.resultado = resultado
                db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionFondos", evaluacionFondos).addOnSuccessListener {
                    mostrarfondos(evaluacionFondos)
                    evaluacionDeportista.evaluacionFondos = evaluacionFondos
                    actualizarestadoprueba()
                }
            }
        }

        binding.btnCalcularCooper.setOnClickListener {
            if (comprobarcamposcooper()){
                val distancia = binding.etCooper.text.toString().toFloat()
                val vo2max = Functions().calcularvo2max(distancia)
                val edad = Functions().calcularEdad(entrenamiento.deportista.fechanacimiento)
                val sexo = entrenamiento.deportista.sexo
                val resultado = Functions().resultadoCooper(distancia, sexo, edad)
                val evaluacionCooper = Evaluacion_Cooper()
                evaluacionCooper.distancia = distancia
                evaluacionCooper.vo2_max = vo2max
                evaluacionCooper.fecha = evaluacionDeportista.fecha
                evaluacionCooper.fechaFormat = Functions().formatearFecha(evaluacionCooper.fecha)
                evaluacionCooper.resultado = resultado
                db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionCooper", evaluacionCooper).addOnSuccessListener {
                    mostrarcooper(evaluacionCooper)
                    evaluacionDeportista.evaluacionCooper = evaluacionCooper
                    actualizarestadoprueba()
                }
            }
        }
    }

    private fun actualizarestadoprueba(){
        if (evaluacionDeportista.evaluacionImc.fecha != "" &&
            evaluacionDeportista.evaluacionFondos.fecha != "" &&
            evaluacionDeportista.evaluacionCooper.fecha != ""){
            evaluacionDeportista.realizado = true
            db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).update("realizado", evaluacionDeportista.realizado).addOnSuccessListener {
                db.collection("users").document(entrenamiento.deportista.email).get().addOnSuccessListener { doc ->
                    var entrenamientos: MutableList<Entrenamiento_DeportistaDB> = arrayListOf()
                    if (doc.get("entrenamientos") != null){
                        entrenamientos = doc.get("entrenamientos") as MutableList<Entrenamiento_DeportistaDB>
                    }
                    val entrenamientoDeportistadb = Entrenamiento_DeportistaDB()
                    entrenamientoDeportistadb.realizado = evaluacionDeportista.realizado
                    entrenamientoDeportistadb.prueba = entrenamiento.prueba
                    entrenamientoDeportistadb.entrenamiento = entrenamiento.entrenamiento.id
                    entrenamientoDeportistadb.fecha = entrenamiento.fecha
                    entrenamientos.set(entrenamiento.posicion, entrenamientoDeportistadb)

                    db.collection("users").document(entrenamiento.deportista.email).update("entrenamientos", entrenamientos)
                }

            }
        }
    }

    private fun comprobarcamposcooper():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etCooper.text.toString())){
            binding.tlCooper.error = "Información requerida"
            valido = false
        } else binding.tlCooper.error = null

        return valido
    }

    private fun comprobarcamposflexiones():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etFondos.text.toString())){
            binding.tlFondos.error = "Información requerida"
            valido = false
        } else binding.tlFondos.error = null

        return valido
    }

    private fun comprobarcamposimc():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etAltura.text.toString())){
            binding.tlAltura.error = "Información requerida"
            valido = false
        } else binding.tlAltura.error = null

        if (TextUtils.isEmpty(binding.etPeso.text.toString())){
            binding.tlPeso.error = "Información requerida"
            valido = false
        } else binding.tlPeso.error = null

        return valido
    }

    private fun cargar() {
        db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).get().addOnSuccessListener {
            doc ->
            if (doc != null) {
                evaluacionDeportista = doc.toObject(Evaluacion_Deportista::class.java)!!
                if (evaluacionDeportista.evaluacionImc.fecha != "") {
                    val evaluacionImc = evaluacionDeportista.evaluacionImc
                    mostrarimc(evaluacionImc)
                }
                if (evaluacionDeportista.evaluacionCooper.fecha != "") {
                    val evaluacionCooper = evaluacionDeportista.evaluacionCooper
                    mostrarcooper(evaluacionCooper)
                }
                if (evaluacionDeportista.evaluacionFondos.fecha != "") {
                    val evaluacionFondos = evaluacionDeportista.evaluacionFondos
                    mostrarfondos(evaluacionFondos)
                }
            }

        }
    }

    private fun mostrarimc(evaluacionImc: Evaluacion_Imc){
        binding.TestIMCMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_check_circle_24,
            0,
            R.drawable.ic_expand_more,
            0
        )
        binding.etAltura.setText(evaluacionImc.altura.toString())
        binding.etPeso.setText(evaluacionImc.peso.toString())
        binding.etAltura.isFocusableInTouchMode = false
        binding.etPeso.isFocusableInTouchMode = false
        binding.etPeso.clearFocus()
        binding.etAltura.clearFocus()
        val resultado = "IMC = ${evaluacionImc.imc} (${evaluacionImc.resultado})"
        binding.txtResultadoIMC.text = resultado
        binding.txtResultadoIMC.isVisible = true
        binding.btnCalcularIMC.isEnabled = false
    }

    private fun mostrarcooper(evaluacionCooper: Evaluacion_Cooper){
        binding.TestCooperMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_check_circle_24,
            0,
            R.drawable.ic_expand_more,
            0
        )
        binding.etCooper.isFocusableInTouchMode = false
        binding.etCooper.clearFocus()
        binding.etCooper.setText(evaluacionCooper.distancia.toString())
        val resultado =
            "VO2max = ${evaluacionCooper.vo2_max} (${evaluacionCooper.resultado}) "
        binding.txtResultadoCooper.text = resultado
        binding.txtResultadoCooper.isVisible = true
        binding.btnCalcularCooper.isEnabled = false
    }

    private fun mostrarfondos(evaluacionFondos: Evaluacion_Fondos){
        binding.TestFuerzaMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_check_circle_24,
            0,
            R.drawable.ic_expand_more,
            0
        )
        binding.etFondos.setText(evaluacionFondos.fondos.toString())
        binding.etFondos.isFocusableInTouchMode = false
        binding.etFondos.clearFocus()
        val resultado =
            "Flexiones = ${evaluacionFondos.fondos} (${evaluacionFondos.resultado}) "
        binding.txtResultadoFlexiones.text = resultado
        binding.txtResultadoFlexiones.isVisible = true
        binding.btnCalcularFlexiones.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (esentrenador){
            val inflate = menuInflater
            inflate.inflate(R.menu.show_entreno_menu, menu)
        }
        return true
    }

    fun delete(){
        db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).delete().addOnSuccessListener{ doc->

            db.collection("users").document(entrenamiento.deportista.email).get().addOnSuccessListener {doc->
                var evaluaciones:MutableList<String> = arrayListOf()
                if (doc.get("evaluacionfisica") != null){
                    evaluaciones = doc.get("evaluacionfisica") as MutableList<String>
                    evaluaciones.remove(entrenamiento.prueba)
                }
                db.collection("users").document(entrenamiento.deportista.email)
                    .update("evaluacionfisica", evaluaciones).addOnSuccessListener{
                        var entrenamientos:MutableList<String> = arrayListOf()
                        if (doc.get("entrenamientos") != null){
                            entrenamientos = doc.get("entrenamientos") as MutableList<String>
                            val index = entrenamiento.posicion
                            entrenamientos.removeAt(index)
                        }
                        db.collection("users").document(entrenamiento.deportista.email)
                            .update("entrenamientos", entrenamientos).addOnSuccessListener{
                                Toast.makeText(this, "Las pruebas físicas del deportista han sido eliminadas con éxito", Toast.LENGTH_LONG).show()
                                onBackPressed()
                            }
                    }.addOnFailureListener {
                        Functions().showSnackSimple(binding.root,"Ha habido un error al eliminar las pruebas físicas")
                    }

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.delete_entreno -> {
                val builder = AlertDialog.Builder(this)

                builder.apply {
                    setTitle("Eliminar prueba del deportista")
                    setMessage("¿Estás seguro?")
                    setPositiveButton(
                        android.R.string.ok){_,_->delete()}
                    setNegativeButton(
                        android.R.string.cancel){_,_->
                        Toast.makeText(
                            context, "La prueba física no ha sido eliminada",
                            Toast.LENGTH_LONG).show()}
                }
                builder.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}