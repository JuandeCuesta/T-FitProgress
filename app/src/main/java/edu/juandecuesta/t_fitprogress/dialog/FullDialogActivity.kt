package edu.juandecuesta.t_fitprogress.dialog

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityFullDialogBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.entrenamientos

import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import android.text.Editable





class FullDialogActivity : AppCompatActivity() {

    lateinit var binding: ActivityFullDialogBinding
    private val recyclerAdapter = RecyclerAdapterSelectEntrenamiento()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val entrenamientoSeleccionado = Entrenamiento()

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

        if (entrenamientos.size > 0){

            binding.tvSinEntrenamSelect.isVisible = false

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
            // +1 because January is zero
            val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
            binding.etFecha.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()

                true
            }
            edu.juandecuesta.t_fitprogress.R.id.action_save -> {
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