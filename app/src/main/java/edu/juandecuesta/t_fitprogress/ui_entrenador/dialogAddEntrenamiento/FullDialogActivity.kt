package edu.juandecuesta.t_fitprogress.ui_entrenador.dialogAddEntrenamiento

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.juandecuesta.t_fitprogress.databinding.ActivityFullDialogBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.entrenamientos

import android.text.Editable
import android.text.TextUtils
import android.widget.Toast
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.utils.Functions


class FullDialogActivity : AppCompatActivity() {

    lateinit var binding: ActivityFullDialogBinding
    private val recyclerAdapter = RecyclerAdapterSelectEntrenamiento()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle("Añadir entrenamiento")

        setUpRecyclerView()

        binding.etFecha.setOnClickListener {
            showDatePickerDialog()
        }

        binding.etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    recyclerAdapter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

    }

    private fun setUpRecyclerView() {

        binding.tvSinEntrenamSelect.isVisible = true
        binding.tlFecha.isVisible = false
        binding.tlSearch.isVisible = false
        binding.rvSelectEntren.isVisible = false

        if (entrenamientos.size > 0){

            binding.tvSinEntrenamSelect.isVisible = false
            binding.tlFecha.isVisible = true
            binding.tlSearch.isVisible = true
            binding.rvSelectEntren.isVisible = true

            binding.rvSelectEntren.setHasFixedSize(true)
            binding.rvSelectEntren.layoutManager = LinearLayoutManager(this)


            recyclerAdapter.RecyclerAdapter(entrenamientos, this)
            binding.rvSelectEntren.adapter = recyclerAdapter
            var dividerItemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
            binding.rvSelectEntren.addItemDecoration(dividerItemDecoration)
        }

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
            binding.etFecha.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun comprobarCampos (): Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etFecha.text.toString())){
            binding.tlFecha.error = "Información requerida"
            valido = false
        } else binding.tlFecha.error = null

        if (recyclerAdapter.itemSelected.id == ""){
            binding.msgSelectEntren.isVisible = true
            valido = false
        } else binding.msgSelectEntren.isVisible = false


        return valido
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()

                true
            }
            edu.juandecuesta.t_fitprogress.R.id.action_save -> {
                if (entrenamientos.size > 0 && comprobarCampos()){
                    var entrenamiento = Entrenamiento_DeportistaDB()
                    entrenamiento.fecha = binding.etFecha.text.toString()
                    entrenamiento.entrenamiento = recyclerAdapter.itemSelected.id
                    db.collection("users").document(deportista.email).get().addOnSuccessListener {
                        doc ->

                        var entrenamientoDBS:MutableList<Entrenamiento_DeportistaDB> = arrayListOf()

                        if (doc.get("entrenamientos") != null){
                            entrenamientoDBS = doc.get("entrenamientos") as MutableList<Entrenamiento_DeportistaDB>
                        }

                        entrenamientoDBS.add(entrenamiento)

                        db.collection("users").document(deportista.email)
                            .update("entrenamientos", entrenamientoDBS).addOnSuccessListener{
                                Toast.makeText(this, "Entrenamiento añadido con exito", Toast.LENGTH_LONG).show()
                                onBackPressed()
                            }.addOnFailureListener {
                                Functions().showSnackSimple(binding.root,"Ha habido un error al añadir el entrenamiento")
                            }
                    }

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(edu.juandecuesta.t_fitprogress.R.menu.fullscreen_dialog, menu)
        return true
    }

}