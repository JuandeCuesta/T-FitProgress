package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityCreateEntrenamientoBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.entrenadorMain
import android.widget.Toast

import android.content.DialogInterface.OnMultiChoiceClickListener
import android.text.TextUtils
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.utils.Functions


class CreateEntrenamientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEntrenamientoBinding
    private val db = FirebaseFirestore.getInstance()
    private var adapterList = addEjercAdapter()

    var ejercicios: MutableList<Ejercicio> = arrayListOf()
    var copy: MutableList<Ejercicio> = arrayListOf()
    val itemsSeleccionados: ArrayList<Int> = ArrayList()
    var ejerciciosSelect: MutableList<Ejercicio> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val type = arrayOf("Resistencia", "Fuerza", "Flexibilidad", "Velocidad")

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, type)
        binding.ettipoEntren.setAdapter(adapter)

        adapterList.adapterList(this, ejerciciosSelect)
        binding.lvExercise.adapter = adapterList


        cargarEjercicios()


        binding.btnAddEjerc.setOnClickListener {
            if (TextUtils.isEmpty(binding.ettipoEntren.text.toString())){
                binding.tlTipoEntren.error = "Información necesaria para seleccionar ejercicios"
            } else{
                binding.tlTipoEntren.error = null
                copy
                ejercicios.clear()
                for (e in copy){
                    if (e.tipo == binding.ettipoEntren.text.toString()){
                        ejercicios.add(e)
                    }
                }
                mostrarDialog()
            }
        }

        binding.btnGuardar.setOnClickListener {
            if (comprobarCamposObligatorios()){
                guardarEntrenamiento()
            }
        }
    }

    private fun guardarEntrenamiento(){

        binding.lyProgress.isVisible = true
        binding.btnGuardar.isVisible = false

        val entrenamiento = Entrenamiento()
        entrenamiento.nombre = binding.etNombreEntren.text.toString()
        entrenamiento.tipo = binding.ettipoEntren.text.toString()
        entrenamiento.descripcion = if (TextUtils.isEmpty(binding.etDescrip.text.toString())) "" else binding.etDescrip.text.toString()
        entrenamiento.series = binding.etSeries.text.toString().toInt()
        entrenamiento.repeticiones = binding.etRep.text.toString().toInt()
        entrenamiento.descanso = if (TextUtils.isEmpty(binding.etDesc.text.toString())) 0 else binding.etDesc.text.toString().toInt()
        ejerciciosSelect.forEach { e -> entrenamiento.ejercicios.add(e.id) }

        db.collection("entrenamientos").add(entrenamiento).addOnSuccessListener {
            it.get().addOnSuccessListener {
                doc->
                val id = doc.id
                db.collection("entrenamientos").document(id).update("id", id).addOnSuccessListener {
                    db.collection("users").document(entrenadorMain.email).get().addOnSuccessListener {
                        document ->
                        var entrenGuardados:MutableList<String> = arrayListOf()

                        if (document.get("entrenamientos") != null){
                            entrenGuardados = document.get("entrenamientos") as MutableList<String>
                        }

                        entrenGuardados.add(id)

                        db.collection("users").document(entrenadorMain.email)
                            .update("entrenamientos", entrenGuardados).addOnSuccessListener{
                                Toast.makeText(this, "Entrenamiento creado con éxito", Toast.LENGTH_LONG).show()
                                onBackPressed()
                            }.addOnFailureListener {
                                binding.lyProgress.isVisible = false
                                binding.btnGuardar.isVisible = true
                                Functions().showSnackSimple(binding.root,"Ha habido un error al crear el entrenamiento")
                            }

                    }
                }
            }
        }
    }

    private fun comprobarCamposObligatorios(): Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etNombreEntren.text.toString())){
            binding.tlNombreEntren.error = "Información requerida"
            valido = false
        } else binding.tlNombreEntren.error = null

        if (TextUtils.isEmpty(binding.ettipoEntren.text.toString())){
            binding.tlTipoEntren.error = "Información requerida"
            valido = false
        } else binding.tlTipoEntren.error = null

        if (TextUtils.isEmpty(binding.etSeries.text.toString())){
            binding.tlSeries.error = "Requerido"
            valido = false
        } else binding.tlSeries.error = null

        if (TextUtils.isEmpty(binding.etRep.text.toString())){
            binding.tlRep.error = "Requerido"
            valido = false
        } else binding.tlRep.error = null


        if (ejerciciosSelect.size == 0){
            binding.txtMsgAlerta.isVisible = true
            valido = false
        } else binding.txtMsgAlerta.isVisible = false

        return valido
    }



    private fun cargarEjercicios(){
        db.collection("users").document(entrenadorMain.email).get().addOnSuccessListener {
                doc ->
            var idEjercicios: MutableList<String> = arrayListOf()
            if (doc.get("ejercicios") != null){
                idEjercicios = doc.get("ejercicios") as MutableList<String>
            }

            for (id in idEjercicios){

                db.collection("ejercicios").document(id).get().addOnSuccessListener {
                        ejerc ->
                    ejerc.toObject(Ejercicio::class.java)?.let { it1 ->
                        ejercicios.add(it1)
                        copy.add(it1)}
                }
            }
        }
    }

    private fun mostrarDialog(){
        itemsSeleccionados.clear()
        var items = arrayOfNulls<CharSequence>(ejercicios.size)
        for (i in 0 until ejercicios.size){
            items[i] = ejercicios[i].nombre

        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Ejercicios")
            .setMultiChoiceItems(items, null,
                OnMultiChoiceClickListener { dialog, which, isChecked ->
                    if (isChecked) {
                        // Guardar indice seleccionado
                        itemsSeleccionados.add(which)

                    } else if (itemsSeleccionados.contains(which)) {
                        // Remover indice sin selección
                        itemsSeleccionados.remove(Integer.valueOf(which))
                    }
                })
        builder.setPositiveButton(android.R.string.ok){_,_ ->
            if (itemsSeleccionados.size > 0){
                ejerciciosSelect = adapterList.getList()
                for (it in itemsSeleccionados){
                    ejerciciosSelect.add(ejercicios.get(it))
                    adapterList.adapterList(this, ejerciciosSelect)
                    adapterList.notifyDataSetChanged()
                }

            }
        }
        builder.setNegativeButton(
            android.R.string.cancel){_,_->}

        builder.show()
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