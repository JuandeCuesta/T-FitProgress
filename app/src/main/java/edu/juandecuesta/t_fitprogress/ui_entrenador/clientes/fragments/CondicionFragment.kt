package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentCondicionBinding
import edu.juandecuesta.t_fitprogress.databinding.FragmentCondicionBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity
import edu.juandecuesta.t_fitprogress.ui_entrenador.dialogAddEntrenamiento.DatePickerFragment
import edu.juandecuesta.t_fitprogress.utils.Functions
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView


class CondicionFragment : Fragment() {

    private lateinit var binding: EntFragmentCondicionBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EntFragmentCondicionBinding.inflate(inflater)
        val root: View = binding.root

        cargarImc()
        cargarFondos()
        cargarVo2()
        binding.etFechaPrueba.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnAddPrueba.setOnClickListener {
            if (verficarCampo()){
                guardar()
            }
        }

        return root
    }

    private fun guardar(){
        var entrenamiento = Entrenamiento_DeportistaDB()
        entrenamiento.fecha = binding.etFechaPrueba.text.toString()
        entrenamiento.entrenamiento = "prueba_fisica"
        db.collection("users").document(ShowClientActivity.deportista.email).get().addOnSuccessListener {
                doc ->

            var entrenamientoDBS:MutableList<Entrenamiento_DeportistaDB> = arrayListOf()
            var evaluaciones:MutableList<Evaluacion_Deportista> = arrayListOf()

            if (doc.get("entrenamientos") != null){
                entrenamientoDBS = doc.get("entrenamientos") as MutableList<Entrenamiento_DeportistaDB>
            }

            if (doc.get("evaluacionfisica") != null){
                evaluaciones = doc.get("evaluacionfisica") as MutableList<Evaluacion_Deportista>
            }
            val eval_dep = Evaluacion_Deportista()
            eval_dep.fecha = entrenamiento.fecha
            entrenamiento.posicion = evaluaciones.size
            evaluaciones.add(eval_dep)
            entrenamientoDBS.add(entrenamiento)
            db.collection("users").document(ShowClientActivity.deportista.email)
                .update("entrenamientos", entrenamientoDBS).addOnSuccessListener{

                    db.collection("users").document(ShowClientActivity.deportista.email)
                        .update("evaluacionfisica", evaluaciones).addOnSuccessListener{
                            Toast.makeText(requireContext(), "Pruebas físicas añadida con exito", Toast.LENGTH_LONG).show()
                            binding.etFechaPrueba.text?.clear()
                        }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Ha habido un error al añadir las pruebas físicas", Toast.LENGTH_LONG).show()
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
                mes = "$month + 1}"
            }


            val selectedDate = "$dia/$mes/$year"
            binding.etFechaPrueba.setText(selectedDate)
        })

        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun cargarImc() {

        val axisData = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"
        )
        val yAxisData = floatArrayOf(18.5f, 20.1f, 21.6f, 22.6f, 23.8f, 23.5f, 24.6f, 23.6f, 28.6f, 26.6f, 26.4f, 24.5f)
        val maxValue = yAxisData.maxOrNull()
        val lastValue = yAxisData.last()
        val valor = String.format("%.2f", lastValue)
        binding.textIMC.text = requireContext().getString(R.string.test_imc,valor)

        var lineChartView: LineChartView = binding.chartImc

        val yAxisValues: MutableList<PointValue> = arrayListOf()
        val axisValues: MutableList<AxisValue> = arrayListOf()


        val line: Line = Line(yAxisValues).setColor(Color.parseColor("#9C27B0"))

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
        if (maxValue != null) {
            viewport.top = maxValue
        }
        lineChartView.maximumViewport = viewport
        lineChartView.currentViewport = viewport


    }

    private fun cargarFondos(){

        val axisData = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"
        )
        val yAxisData = intArrayOf(23, 24, 20, 33, 35, 40, 38, 44, 42, 46, 48, 50)
        val maxValue = yAxisData.maxOrNull()
        val lastValue = yAxisData.last()
        binding.textFondos.text = requireContext().getString(R.string.test_fuerza,lastValue.toString())

        var lineChartView: LineChartView = binding.chartFondos

        val yAxisValues: MutableList<PointValue> = arrayListOf()
        val axisValues: MutableList<AxisValue> = arrayListOf()


        val line: Line = Line(yAxisValues).setColor(ContextCompat.getColor(requireContext(),R.color.red))

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
        if (maxValue != null) {
            viewport.top = maxValue.toFloat()
        }
        lineChartView.maximumViewport = viewport
        lineChartView.currentViewport = viewport

    }

    private fun cargarVo2(){
        val axisData = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"
        )
        val yAxisData = floatArrayOf(23.5f, 35.0f, 35.2f, 25f, 36f, 40f, 42f, 38f, 45f, 40f, 38f, 35f)
        val maxValue = yAxisData.maxOrNull()
        val lastValue = yAxisData.last()
        val valor = String.format("%.2f", lastValue)
        binding.textCooper.text = requireContext().getString(R.string.test_cooper,valor)


        var lineChartView: LineChartView = binding.chartCooper

        val yAxisValues: MutableList<PointValue> = arrayListOf()
        val axisValues: MutableList<AxisValue> = arrayListOf()


        val line: Line = Line(yAxisValues).setColor(ContextCompat.getColor(requireContext(),R.color.verde))

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
        yAxis.name = "VO2 max"
        yAxis.textColor = Color.parseColor("#03A9F4")
        yAxis.textSize = 16
        data.axisYLeft = yAxis

        lineChartView.lineChartData = data
        val viewport = Viewport(lineChartView.maximumViewport)
        if (maxValue != null) {
            viewport.top = maxValue
        }
        lineChartView.maximumViewport = viewport
        lineChartView.currentViewport = viewport
    }

}