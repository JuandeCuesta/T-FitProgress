package edu.juandecuesta.t_fitprogress.ui_deportista.evaluacion

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.*
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity
import edu.juandecuesta.t_fitprogress.utils.Functions


class EvaluacionFragment : Fragment() {

    private var _binding: FragmentEvaluacionBinding? = null

    private val binding get() = _binding!!

    private val eval_dep = Evaluacion_Deportista()
    private val entrenamiento = Entrenamiento_DeportistaDB()
    private var posicion = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEvaluacionBinding.inflate(inflater, container, false)

        if (deportistaMain.sexo == "Hombre") binding.etTipoFlexion.setText("Flexiones normales") else binding.etTipoFlexion.setText("Flexiones modificadas")
        binding.txtDeportista.isVisible = false
        binding.txtFechaPrueba.text = Functions().mostrarFecha()

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
                evaluacionImc.fecha = Functions().mostrarFecha()
                evaluacionImc.fechaFormat = Functions().formatearFecha(evaluacionImc.fecha)
                if (entrenamiento.prueba == ""){
                    eval_dep.evaluacionImc = evaluacionImc
                    crearevaluacion()
                }else {
                    eval_dep.evaluacionImc = evaluacionImc
                    actualizarimc(eval_dep.evaluacionImc)
                }
            }
        }

        binding.btnCalcularFlexiones.setOnClickListener {
            if (comprobarcamposflexiones()){
                val flexiones = binding.etFondos.text.toString().toInt()
                val prueba = binding.etTipoFlexion.text.toString()
                val edad = Functions().calcularEdad(deportistaMain.fechanacimiento)
                val resultado = Functions().resultadoFuerza(flexiones,prueba, edad)
                val evaluacionFondos = Evaluacion_Fondos()
                evaluacionFondos.fondos = flexiones
                evaluacionFondos.fecha = Functions().mostrarFecha()
                evaluacionFondos.fechaFormat = Functions().formatearFecha(evaluacionFondos.fecha)
                evaluacionFondos.resultado = resultado

                if (entrenamiento.prueba == ""){
                    eval_dep.evaluacionFondos = evaluacionFondos
                    crearevaluacion()
                }else {
                    eval_dep.evaluacionFondos = evaluacionFondos
                    actualizarfondos(eval_dep.evaluacionFondos)
                }
            }
        }

        binding.btnCalcularCooper.setOnClickListener {
            if (comprobarcamposcooper()){
                val distancia = binding.etCooper.text.toString().toFloat()
                val vo2max = Functions().calcularvo2max(distancia)
                val edad = Functions().calcularEdad(deportistaMain.fechanacimiento)
                val sexo = deportistaMain.sexo
                val resultado = Functions().resultadoCooper(distancia, sexo, edad)
                val evaluacionCooper = Evaluacion_Cooper()
                evaluacionCooper.distancia = distancia
                evaluacionCooper.vo2_max = vo2max
                evaluacionCooper.fecha = Functions().mostrarFecha()
                evaluacionCooper.fechaFormat = Functions().formatearFecha(evaluacionCooper.fecha)
                evaluacionCooper.resultado = resultado
                if (entrenamiento.prueba == ""){
                    eval_dep.evaluacionCooper = evaluacionCooper
                    crearevaluacion()
                }else {
                    eval_dep.evaluacionCooper = evaluacionCooper
                    actualizarcooper(eval_dep.evaluacionCooper)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.etAltura.text?.clear()
        binding.etPeso.text?.clear()
        binding.etFondos.text?.clear()
        binding.etCooper.text?.clear()
    }

    private fun actualizarcooper(evaluacionCooper: Evaluacion_Cooper){
        db.collection("users").document(deportistaMain.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionCooper", evaluacionCooper).addOnSuccessListener {
            mostrarcooper(evaluacionCooper)
            actualizarestadoprueba()
        }
    }

    private fun actualizarfondos(evaluacionFondos: Evaluacion_Fondos){
        db.collection("users").document(deportistaMain.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionFondos", evaluacionFondos).addOnSuccessListener {
            mostrarfondos(evaluacionFondos)
            actualizarestadoprueba()
        }
    }

    private fun actualizarimc(evaluacionImc: Evaluacion_Imc){
        db.collection("users").document(deportistaMain.email).collection("evaluaciones").document(entrenamiento.prueba).update("evaluacionImc", evaluacionImc).addOnSuccessListener {
            mostrarimc(evaluacionImc)
            actualizarestadoprueba()
        }
    }

    private fun crearevaluacion(){
        entrenamiento.fecha = Functions().mostrarFecha()
        entrenamiento.entrenamiento = "prueba_fisica"

        eval_dep.fecha = entrenamiento.fecha

        db.collection("users").document(deportistaMain.email).collection("evaluaciones").add(eval_dep).addOnSuccessListener {
                it -> db.collection("users").document(deportistaMain.email).get().addOnSuccessListener { doc ->
            var entrenamientoDBS:MutableList<Entrenamiento_DeportistaDB> = arrayListOf()
            var evaluaciones:MutableList<String> = arrayListOf()
            if (doc.get("entrenamientos") != null){
                entrenamientoDBS = doc.get("entrenamientos") as MutableList<Entrenamiento_DeportistaDB>
            }

            if (doc.get("evaluacionfisica") != null){
                evaluaciones = doc.get("evaluacionfisica") as MutableList<String>
            }
            evaluaciones.add(it.id)
            entrenamiento.prueba = it.id
            entrenamientoDBS.add(entrenamiento)
            db.collection("users").document(deportistaMain.email)
                .update("entrenamientos", entrenamientoDBS).addOnSuccessListener{dc->

                    db.collection("users").document(deportistaMain.email)
                        .update("evaluacionfisica", evaluaciones).addOnSuccessListener {
                            actualizarimc(eval_dep.evaluacionImc)
                        }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Ha habido un error al añadir las pruebas físicas", Toast.LENGTH_LONG).show()
                }

        }
        }
    }

    private fun actualizarestadoprueba(){
        if (eval_dep.evaluacionImc.fecha != "" &&
            eval_dep.evaluacionFondos.fecha != "" &&
            eval_dep.evaluacionCooper.fecha != ""){
            eval_dep.realizado = true
            db.collection("users").document(deportistaMain.email).collection("evaluaciones").document(entrenamiento.prueba).update("realizado", eval_dep.realizado).addOnSuccessListener {
                db.collection("users").document(deportistaMain.email).get().addOnSuccessListener { doc ->

                    val entrenamientoDeportistadb = Entrenamiento_DeportistaDB()
                    entrenamientoDeportistadb.realizado = eval_dep.realizado
                    entrenamientoDeportistadb.prueba = entrenamiento.prueba
                    entrenamientoDeportistadb.entrenamiento = entrenamiento.entrenamiento
                    entrenamientoDeportistadb.fecha = entrenamiento.fecha
                    posicion = 0
                    for (e:Entrenamiento_DeportistaDB in deportistaMain.entrenamientos!!){
                        if (e.prueba == entrenamientoDeportistadb.prueba){
                            deportistaMain.entrenamientos!![posicion] = entrenamientoDeportistadb
                        }
                        posicion++
                    }

                    db.collection("users").document(deportistaMain.email).update("entrenamientos", deportistaMain.entrenamientos)
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

}