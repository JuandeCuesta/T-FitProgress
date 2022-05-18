package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.databinding.DepActivityShowEntrenamientoBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.ui_deportista.entrenamientos.RecyclerAdapterEjerciciosEntrenamientos
import edu.juandecuesta.t_fitprogress.utils.Functions

class ShowEntrenoDeportistaActivity : AppCompatActivity() {
    private lateinit var binding: DepActivityShowEntrenamientoBinding
    private var entrenamiento = Entrenamiento_Deportista()
    var ejerciciosEntrenamiento: MutableList<Ejercicio> = arrayListOf()

    private val recyclerAdapter = RecyclerAdapterEjerciciosEntrenamientos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DepActivityShowEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento_Deportista
        val titulo = "Deportista ${entrenamiento.deportista.nombre}"
        title = titulo

        cargarDatos()

        setUpRecyclerView()
        loadRecyclerViewAdapter()

    }

    private fun setUpRecyclerView() {


        if (entrenamiento.entrenamiento.ejercicios.size > 0){

            binding.rvExercise.setHasFixedSize(true)
            binding.rvExercise.layoutManager = LinearLayoutManager(this)


            recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
            binding.rvExercise.adapter = recyclerAdapter
            var dividerItemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
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
                            }
                        }
                    }

                }
        }
    }

    fun cargarDatos(){
        binding.nombreShow.text = entrenamiento.entrenamiento.nombre
        binding.nombreShow.isVisible = true
        val tipo = "Entrenamiento de ${entrenamiento.entrenamiento.tipo}"
        binding.etTipoVista.setText(tipo)
        binding.etTipoVista.setText(entrenamiento.entrenamiento.tipo)

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
        if (entrenamiento.realizado){
            binding.selectionButton.check(R.id.btnSiRealizado)
        } else binding.selectionButton.check(R.id.btnNoRealizado)
        binding.btnNoRealizado.isClickable = false
        binding.btnSiRealizado.isClickable = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.show_entreno_menu, menu)
        return true
    }

    fun delete(){
        db.collection("users").document(entrenamiento.deportista.email).get().addOnSuccessListener{ doc->

            var entrenamientos:MutableList<String> = arrayListOf()

            if (doc.get("entrenamientos") != null){
                entrenamientos = doc.get("entrenamientos") as MutableList<String>
                val index = entrenamiento.posicion
                entrenamientos.removeAt(index)
            }

            db.collection("users").document(entrenamiento.deportista.email)
                .update("entrenamientos", entrenamientos).addOnSuccessListener{
                    Toast.makeText(this, "El entrenamiento del deportista ha sido eliminado con éxito", Toast.LENGTH_LONG).show()
                    onBackPressed()
                }.addOnFailureListener {
                    Functions().showSnackSimple(binding.root,"Ha habido un error al eliminar el entrenamiento")
                }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                    onBackPressed()
                true
            }
            R.id.delete_entreno -> {
                val builder = AlertDialog.Builder(this)

                builder.apply {
                    setTitle("Eliminar entrenamiento del deportista")
                    setMessage("¿Estás seguro?")
                    setPositiveButton(
                        android.R.string.ok){_,_->delete()}
                    setNegativeButton(
                        android.R.string.cancel){_,_->
                        Toast.makeText(
                            context, "El entrenamiento no ha sido eliminado",
                            Toast.LENGTH_LONG).show()}
                }
                builder.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}