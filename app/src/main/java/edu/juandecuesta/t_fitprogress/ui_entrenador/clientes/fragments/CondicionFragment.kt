package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentCondicionBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Cooper
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Deportista
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Fondos
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Imc
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.dialogAddEntrenamiento.DatePickerFragment
import edu.juandecuesta.t_fitprogress.utils.Functions
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView


class CondicionFragment : Fragment() {

    private var _binding: EntFragmentCondicionBinding? = null
    private val binding get() = _binding!!
    private var evaluaciones:MutableList<Evaluacion_Deportista> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EntFragmentCondicionBinding.inflate(inflater)
        val root: View = binding.root
        cargarevaluaciones()
        binding.etFechaPrueba.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnAddPrueba.setOnClickListener {
            if (verficarCampo()){
                binding.idProgress.isVisible = true
                binding.btnAddPrueba.setText("Añadiendo prueba física...")
                guardar()
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun guardar(){
        var entrenamiento = Entrenamiento_DeportistaDB()
        entrenamiento.fecha = binding.etFechaPrueba.text.toString()
        entrenamiento.entrenamiento = "prueba_fisica"
        val eval_dep = Evaluacion_Deportista()
        eval_dep.fecha = entrenamiento.fecha

        db.collection("users").document(ShowClientActivity.deportista.email).collection("evaluaciones").add(eval_dep).addOnSuccessListener {
            it -> db.collection("users").document(ShowClientActivity.deportista.email).get().addOnSuccessListener { doc ->
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
                db.collection("users").document(ShowClientActivity.deportista.email)
                    .update("entrenamientos", entrenamientoDBS).addOnSuccessListener{dc->

                        db.collection("users").document(ShowClientActivity.deportista.email)
                            .update("evaluacionfisica", evaluaciones).addOnSuccessListener{
                                binding.etFechaPrueba.text?.clear()
                                binding.idProgress.isVisible = false
                                binding.btnAddPrueba.setText("Añadir fecha")
                                Toast.makeText(requireContext(), "Pruebas físicas añadidas con éxito", Toast.LENGTH_LONG).show()
                            }

                    }.addOnFailureListener {
                        binding.idProgress.isVisible = false
                        binding.btnAddPrueba.setText("Añadir fecha")
                        Toast.makeText(requireContext(), "Ha habido un error al añadir las pruebas físicas", Toast.LENGTH_LONG).show()
                    }

            }
        }
    }

    private fun verficarCampo():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etFechaPrueba.text.toString())){
            binding.tlFechaPrueba.error = "Información requerida"
            valido = false
        } else binding.tlFechaPrueba.error = null
        return valido
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->

            var dia = ""
            var mes = ""

            if (day < 10){
                dia = "0${day}"
            }else {
                dia = "$day"
            }

            if ((month + 1) < 10){
                mes = "0${month + 1}"
            }else {
                mes = "${month + 1}"
            }


            val selectedDate = "$dia/$mes/$year"
            binding.etFechaPrueba.setText(selectedDate)
        })

        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun cargarevaluaciones() {

        db.collection("users").document(deportista.email).collection("evaluaciones").addSnapshotListener { doc, error ->
            if (error != null){
                Log.w(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (doc != null){
                evaluaciones.clear()
                for (dc in doc.documents){
                    dc.toObject(Evaluacion_Deportista::class.java)?.let { evaluaciones.add(it) }
                }

                if (_binding != null){
                    cargarImc()
                    cargarFondos()
                    cargarVo2()
                }
                for (dc in doc.documents){
                    db.collection("users").document(deportista.email).collection("evaluaciones").whereEqualTo(
                        FieldPath.documentId(), dc.id).addSnapshotListener { value, exc ->
                        if (exc != null){
                            Log.w(ContentValues.TAG, "Listen failed.", exc)
                            return@addSnapshotListener
                        }
                        if (value != null){
                            for (vc in value.documentChanges){
                                when (vc.type){
                                    DocumentChange.Type.MODIFIED -> {
                                        val evaluacion = vc.document.toObject(Evaluacion_Deportista::class.java)
                                        evaluacion.id = vc.document.id

                                        for (i in 0 until evaluaciones.size){
                                            if (evaluaciones[i].id == evaluacion.id){
                                                evaluaciones.set(i, evaluacion)
                                            }
                                        }
                                        if (_binding != null){
                                            cargarImc()
                                            cargarFondos()
                                            cargarVo2()
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun cargarImc() {
        val evaluacionesImc:MutableList<Evaluacion_Imc> = arrayListOf()
        evaluaciones.forEach { e ->
            if (e.evaluacionImc.fecha != ""){
                evaluacionesImc.add(e.evaluacionImc)
            }
        }

        if (evaluacionesImc.size > 0){

            evaluacionesImc.sortBy { e -> e.fechaFormat }
            val axisData = arrayListOf<String>()
            val yAxisData = arrayListOf<Float>()
            var datos = 0
            evaluacionesImc.forEach { e ->
                var mes = Functions().obtenerMes(e.fecha, requireContext())
                if (datos != 0 && axisData[(datos-1)] == mes){
                    axisData[datos-1] = mes
                    yAxisData[datos-1] = e.imc
                }else {
                    axisData.add(mes)
                    yAxisData.add(e.imc)
                    datos++
                }
            }


            val lastValue = yAxisData.last()
            val valor = String.format("%.2f", lastValue)
            val resultado = Functions().resultadoIMC(valor.replace(",",".").toFloat())
            binding.textIMC.text = requireContext().getString(R.string.test_imc,valor,resultado)

            var lineChartView: LineChartView = binding.chartImc

            if (axisData.size > 1){

                if (axisData.size > 6){
                    do{
                        axisData.removeAt(0)
                        yAxisData.removeAt(0)
                    }while(axisData.size > 6)
                }

                val yAxisValues: MutableList<PointValue> = arrayListOf()
                val axisValues: MutableList<AxisValue> = arrayListOf()
                val minValue = yAxisData.minOrNull()
                val maxValue = yAxisData.maxOrNull()

                val line: Line = Line(yAxisValues).setColor(Color.parseColor("#9C27B0"))
                line.setHasLabels(true)
                line.setFilled(true)

                for (i in axisData.indices) {
                    val float = i.toFloat()
                    axisValues.add(i, AxisValue(float).setLabel(axisData[i]))
                }

                for (i in yAxisData.indices) {
                    yAxisValues.add(PointValue(i.toFloat(), yAxisData[i].toFloat()))
                }

                val lines: MutableList<Line> = ArrayList()
                lines.add(line)

                val data = LineChartData()
                data.lines = lines

                val axis = Axis()
                axis.values = axisValues
                axis.textSize = 16
                axis.textColor = Color.parseColor("#03A9F4")
                data.axisXBottom = axis

                val yAxis = Axis()
                yAxis.name = "IMC"
                yAxis.textColor = Color.parseColor("#03A9F4")
                yAxis.textSize = 16
                data.axisYLeft = yAxis

                lineChartView.lineChartData = data
                val viewport = Viewport(lineChartView.maximumViewport)
                if (maxValue != null && minValue != null) {
                    viewport.top = (maxValue + 5)
                    viewport.bottom = (minValue - 5)
                }
                lineChartView.maximumViewport = viewport
                lineChartView.currentViewport = viewport
                lineChartView.isZoomEnabled = false

            } else {
                lineChartView.isVisible = false
            }
        } else {
            binding.chartImc.isVisible = false
            binding.textIMC.setText("SIN REGISTROS DEL IMC")
        }

    }

    private fun cargarFondos(){

        val evaluacionFondos:MutableList<Evaluacion_Fondos> = arrayListOf()
        evaluaciones.forEach { e ->
            if (e.evaluacionFondos.fecha != ""){
                evaluacionFondos.add(e.evaluacionFondos)
            }
        }

        if (evaluacionFondos.size > 0){

            evaluacionFondos.sortBy { e -> e.fechaFormat }
            val axisData = arrayListOf<String>()
            val yAxisData = arrayListOf<Float>()
            var datos = 0
            evaluacionFondos.forEach { e ->
                var mes = Functions().obtenerMes(e.fecha, requireContext())
                if (datos != 0 && axisData[(datos-1)] == mes){
                    axisData[datos-1] = mes
                    yAxisData[datos-1] = e.fondos.toFloat()
                }else {
                    axisData.add(mes)
                    yAxisData.add(e.fondos.toFloat())
                    datos++
                }
            }

            val lastValue = yAxisData.last()
            val valor = String.format("%.0f", lastValue)
            val resultado = evaluacionFondos.last().resultado
            binding.textFondos.text = requireContext().getString(R.string.test_fuerza,valor,resultado)

            var lineChartView: LineChartView = binding.chartFondos

            if (axisData.size > 1){

                if (axisData.size > 6){
                    do{
                        axisData.removeAt(0)
                        yAxisData.removeAt(0)
                    }while(axisData.size > 6)
                }

                val yAxisValues: MutableList<PointValue> = arrayListOf()
                val axisValues: MutableList<AxisValue> = arrayListOf()
                val minValue = yAxisData.minOrNull()
                val maxValue = yAxisData.maxOrNull()


                val line: Line = Line(yAxisValues).setColor(ContextCompat.getColor(requireContext(),R.color.red))
                line.setHasLabels(true)
                line.setFilled(true)

                for (i in axisData.indices) {
                    val float = i.toFloat()
                    axisValues.add(i, AxisValue(float).setLabel(axisData[i]))
                }

                for (i in yAxisData.indices) {
                    yAxisValues.add(PointValue(i.toFloat(), yAxisData[i].toFloat()))
                }

                val lines: MutableList<Line> = ArrayList()
                lines.add(line)

                val data = LineChartData()
                data.lines = lines

                val axis = Axis()
                axis.values = axisValues
                axis.textSize = 16
                axis.textColor = Color.parseColor("#03A9F4")
                data.axisXBottom = axis

                val yAxis = Axis()
                yAxis.name = "Flexiones"
                yAxis.textColor = Color.parseColor("#03A9F4")
                yAxis.textSize = 16
                data.axisYLeft = yAxis

                lineChartView.lineChartData = data
                val viewport = Viewport(lineChartView.maximumViewport)
                if (maxValue != null && minValue != null) {
                    viewport.top = (maxValue + 5)
                    viewport.bottom = (minValue - 5)
                }
                lineChartView.maximumViewport = viewport
                lineChartView.currentViewport = viewport
                lineChartView.isZoomEnabled = false

            } else {
                lineChartView.isVisible = false
            }
        } else {
            binding.chartFondos.isVisible = false
            binding.textFondos.setText("SIN REGISTROS DE FUERZA")
        }

    }

    private fun cargarVo2(){
        val evaluacionCooper:MutableList<Evaluacion_Cooper> = arrayListOf()
        evaluaciones.forEach { e ->
            if (e.evaluacionCooper.fecha != ""){
                evaluacionCooper.add(e.evaluacionCooper)
            }
        }

        if (evaluacionCooper.size > 0){

            evaluacionCooper.sortBy { e -> e.fechaFormat }
            val axisData = arrayListOf<String>()
            val yAxisData = arrayListOf<Float>()
            var datos = 0
            evaluacionCooper.forEach { e ->
                var mes = Functions().obtenerMes(e.fecha, requireContext())
                if (datos != 0 && axisData[(datos-1)] == mes){
                    axisData[datos-1] = mes
                    yAxisData[datos-1] = e.vo2_max.toFloat()
                }else {
                    axisData.add(mes)
                    yAxisData.add(e.vo2_max.toFloat())
                    datos++
                }
            }

            val lastValue = yAxisData.last()
            val valor = String.format("%.2f", lastValue)
            val resultado = evaluacionCooper.last().resultado
            binding.textCooper.text = requireContext().getString(R.string.test_cooper,valor,resultado)

            var lineChartView: LineChartView = binding.chartCooper

            if (axisData.size > 1){

                if (axisData.size > 6){
                    do{
                        axisData.removeAt(0)
                        yAxisData.removeAt(0)
                    }while(axisData.size > 6)
                }

                val yAxisValues: MutableList<PointValue> = arrayListOf()
                val axisValues: MutableList<AxisValue> = arrayListOf()
                val minValue = yAxisData.minOrNull()
                val maxValue = yAxisData.maxOrNull()

                val line: Line = Line(yAxisValues).setColor(ContextCompat.getColor(requireContext(),R.color.verde))
                line.setHasLabels(true)
                line.setFilled(true)

                for (i in axisData.indices) {
                    val float = i.toFloat()
                    axisValues.add(i, AxisValue(float).setLabel(axisData[i]))
                }

                for (i in yAxisData.indices) {
                    yAxisValues.add(PointValue(i.toFloat(), yAxisData[i].toFloat()))
                }

                val lines: MutableList<Line> = ArrayList()
                lines.add(line)

                val data = LineChartData()
                data.lines = lines

                val axis = Axis()
                axis.values = axisValues
                axis.textSize = 16
                axis.textColor = Color.parseColor("#03A9F4")
                data.axisXBottom = axis

                val yAxis = Axis()
                yAxis.name = "VO2max"
                yAxis.textColor = Color.parseColor("#03A9F4")
                yAxis.textSize = 16
                data.axisYLeft = yAxis

                lineChartView.lineChartData = data
                val viewport = Viewport(lineChartView.maximumViewport)
                if (maxValue != null && minValue != null) {
                    viewport.top = (maxValue + 5)
                    viewport.bottom = (minValue - 5)
                }
                lineChartView.maximumViewport = viewport
                lineChartView.currentViewport = viewport
                lineChartView.isZoomEnabled = false

            } else {
                lineChartView.isVisible = false
            }
        } else {
            binding.chartCooper.isVisible = false
            binding.textCooper.setText("SIN REGISTROS DE VO2max")
        }

    }

}