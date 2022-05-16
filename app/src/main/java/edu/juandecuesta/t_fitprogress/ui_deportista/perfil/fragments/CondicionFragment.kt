package edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.juandecuesta.t_fitprogress.databinding.FragmentCondicionBinding
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.LineChartView
import android.graphics.Color
import androidx.core.content.ContextCompat
import edu.juandecuesta.t_fitprogress.R


class CondicionFragment : Fragment() {

    private lateinit var binding: FragmentCondicionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCondicionBinding.inflate(inflater)
        val root: View = binding.root

        cargarImc()
        cargarFondos()
        cargarVo2()

        return root
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