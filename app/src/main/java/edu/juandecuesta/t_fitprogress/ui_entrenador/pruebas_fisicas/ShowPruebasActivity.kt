package edu.juandecuesta.t_fitprogress.ui_entrenador.pruebas_fisicas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.model.Evaluacion_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity
import edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes.CreateMessageActivity

class ShowPruebasActivity : AppCompatActivity() {
    private lateinit var binding:FragmentEvaluacionBinding
    var entrenamiento = Entrenamiento_Deportista()
    var evaluacionDeportista = Evaluacion_Deportista()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEvaluacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento_Deportista
        val nombreCompleto = "${entrenamiento.deportista.nombre} ${entrenamiento.deportista.apellido}"
        binding.txtDeportista.text = nombreCompleto
        binding.txtFechaPrueba.text = entrenamiento.fecha

        val type = arrayOf("Flexiones normales", "Flexiones modificadas")
        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, type)
        binding.etTIpoFlexion.setAdapter(adapter)
        cargar()


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


    }

    private fun cargar() {
        db.collection("users").document(entrenamiento.deportista.email).collection("evaluaciones").document(entrenamiento.prueba).get().addOnSuccessListener {
            doc ->
            if (doc != null) {
                evaluacionDeportista = doc.toObject(Evaluacion_Deportista::class.java)!!
                if (evaluacionDeportista.evaluacionImc.fecha != "") {
                    val evaluacionImc = evaluacionDeportista.evaluacionImc
                    binding.TestIMCMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_baseline_check_circle_24,
                        0,
                        R.drawable.ic_expand_more,
                        0
                    )
                    binding.etAltura.setText(evaluacionImc.altura.toString())
                    binding.etPeso.setText(evaluacionImc.peso.toString())
                    val resultado = "IMC = ${evaluacionImc.imc} (${evaluacionImc.resultado})"
                    binding.txtResultadoIMC.text = resultado
                    binding.btnCalcularIMC.isEnabled = false
                }
                if (evaluacionDeportista.evaluacionCooper.fecha != "") {
                    val evaluacionCooper = evaluacionDeportista.evaluacionCooper
                    binding.TestCooperMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_baseline_check_circle_24,
                        0,
                        R.drawable.ic_expand_more,
                        0
                    )
                    binding.etCooper.setText(evaluacionCooper.distancia.toString())
                    val resultado =
                        "VO2max = ${evaluacionCooper.vo2_max} (${evaluacionCooper.resultado}) "
                    binding.txtResultadoCooper.text = resultado
                    binding.btnCalcularCooper.isEnabled = false
                }
                if (evaluacionDeportista.evaluacionFondos.fecha != "") {
                    val evaluacionFondos = evaluacionDeportista.evaluacionFondos
                    binding.TestFuerzaMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_baseline_check_circle_24,
                        0,
                        R.drawable.ic_expand_more,
                        0
                    )
                    binding.etFondos.setText(evaluacionFondos.fondos.toString())
                    val resultado =
                        "Flexiones = ${evaluacionFondos.fondos} (${evaluacionFondos.resultado}) "
                    binding.txtResultadoCooper.text = resultado
                    binding.btnCalcularCooper.isEnabled = false
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}