package edu.juandecuesta.t_fitprogress.ui_deportista.entrenamientos

import android.content.ContentValues
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityEditEntrenamientoBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.databinding.ActivityShowEntrenamientoBinding
import edu.juandecuesta.t_fitprogress.utils.Functions

class ShowEntrenamientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowEntrenamientoBinding
    private val db = FirebaseFirestore.getInstance()
    private var entrenamiento = Entrenamiento()
    var ejercicios: MutableList<Ejercicio> = arrayListOf()
    var ejerciciosEntrenamiento: MutableList<Ejercicio> = arrayListOf()

    private val recyclerAdapter = RecyclerAdapterEjerciciosEntrenamientos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento
        setTitle(entrenamiento.nombre)
        cargarDatos()

        setUpRecyclerView()
        loadRecyclerViewAdapter()

    }


    private fun setUpRecyclerView() {

        if (entrenamiento.ejercicios.size > 0){

            binding.rvExercise.setHasFixedSize(true)
            binding.rvExercise.layoutManager = LinearLayoutManager(this)


            recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
            binding.rvExercise.adapter = recyclerAdapter
            var dividerItemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
            binding.rvExercise.addItemDecoration(dividerItemDecoration)
        }

    }


    fun loadRecyclerViewAdapter(){

        for (idEjerc:String in entrenamiento.ejercicios){

            db.collection("ejercicios").whereEqualTo(FieldPath.documentId(),idEjerc)
                .addSnapshotListener{doc, exc ->
                    if (exc != null){
                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                        return@addSnapshotListener
                    }

                    if (doc != null){

                        for (dc in doc.documentChanges){
                            when (dc.type){
                                DocumentChange.Type.ADDED -> {
                                    val ejerc = doc.documents[0].toObject(
                                        Ejercicio::class.java)
                                    ejerciciosEntrenamiento.add(ejerc!!)
                                    recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
                                    recyclerAdapter.notifyDataSetChanged()
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val ejerc = doc.documents[0].toObject(
                                        Ejercicio::class.java)
                                    for (i in 0 until ejerciciosEntrenamiento.size){
                                        if (ejerciciosEntrenamiento[i].id == ejerc!!.id){
                                            ejerciciosEntrenamiento.set(i,ejerc)
                                            recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
                                            recyclerAdapter.notifyDataSetChanged()
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
        }
    }

    fun cargarDatos(){
        val tipo = "Entrenamiento de ${entrenamiento.tipo}"
        binding.etTipoVista.setText(tipo)

        if (entrenamiento.descripcion != ""){
            binding.tlDescripc.isVisible = true
            binding.etDescrip.setText(entrenamiento.descripcion)
        }

        binding.etSeries.setText(entrenamiento.series.toString())
        binding.etRep.setText(entrenamiento.repeticiones.toString())
        if (entrenamiento.descanso != 0){
            binding.tlDesc.isVisible = true
            binding.etDesc.setText(entrenamiento.descanso.toString())
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}