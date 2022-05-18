package edu.juandecuesta.t_fitprogress.ui_deportista.entrenamientos

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.databinding.DepActivityShowEntrenamientoBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista

class ShowEntrenamientoActivity : AppCompatActivity() {
    private lateinit var binding: DepActivityShowEntrenamientoBinding
    private var entrenamiento = Entrenamiento_Deportista()
    var ejercicios: MutableList<Ejercicio> = arrayListOf()
    var ejerciciosEntrenamiento: MutableList<Ejercicio> = arrayListOf()

    private val recyclerAdapter = RecyclerAdapterEjerciciosEntrenamientos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DepActivityShowEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento_Deportista
        title = entrenamiento.entrenamiento.nombre

        if (entrenamiento.realizado){
            binding.selectionButton.check(R.id.btnSiRealizado)
        } else binding.selectionButton.check(R.id.btnNoRealizado)

        cargarDatos()

        setUpRecyclerView()
        loadRecyclerViewAdapter()
        
        binding.selectionButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked){
                when (checkedId){
                    R.id.btnNoRealizado -> {
                        deportistaMain.entrenamientos?.get(entrenamiento.posicion)?.realizado  = false
                        db.collection("users").document(deportistaMain.email).update("entrenamientos", deportistaMain.entrenamientos).addOnSuccessListener {
                            Toast.makeText(this,"Datos modificados correctamente", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this,"Ha ocurrido un error inesperado", Toast.LENGTH_LONG).show()
                        }
                    }
                    R.id.btnSiRealizado -> {
                        deportistaMain.entrenamientos?.get(entrenamiento.posicion)?.realizado = true
                        db.collection("users").document(deportistaMain.email).update("entrenamientos", deportistaMain.entrenamientos).addOnSuccessListener {
                            Toast.makeText(this,"Datos modificados correctamente", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this,"Ha ocurrido un error inesperado", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    private fun setUpRecyclerView() {

        if (entrenamiento.entrenamiento.ejercicios.size > 0){

            binding.rvExercise.setHasFixedSize(true)
            binding.rvExercise.layoutManager = LinearLayoutManager(this)


            recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
            binding.rvExercise.adapter = recyclerAdapter
            val dividerItemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
            binding.rvExercise.addItemDecoration(dividerItemDecoration)
        }

    }


    fun loadRecyclerViewAdapter(){

        for (idEjerc:String in entrenamiento.entrenamiento.ejercicios){

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
                                else -> {}
                            }
                        }
                    }

                }
        }
    }

    fun cargarDatos(){
        val tipo = "Entrenamiento de ${entrenamiento.entrenamiento.tipo}"
        binding.etTipoVista.setText(tipo)

        if (entrenamiento.entrenamiento.descripcion != ""){
            binding.tlDescripc.isVisible = true
            binding.etDescrip.setText(entrenamiento.entrenamiento.descripcion)
        }

        binding.etSeries.setText(entrenamiento.entrenamiento.series.toString())
        binding.etRep.setText(entrenamiento.entrenamiento.repeticiones.toString())
        if (entrenamiento.entrenamiento.descanso != 0){
            binding.tlDesc.isVisible = true
            binding.etDesc.setText(entrenamiento.entrenamiento.descanso.toString())
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