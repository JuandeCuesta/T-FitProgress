package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

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
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.utils.Functions

class EditEntrenamientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEntrenamientoBinding
    private val db = FirebaseFirestore.getInstance()
    private var entrenamiento = Entrenamiento()
    var ejercicios: MutableList<Ejercicio> = arrayListOf()
    var ejerciciosEntrenamiento: MutableList<Ejercicio> = arrayListOf()
    var ejerciciosSelect: MutableList<Ejercicio> = arrayListOf()
    val itemsSeleccionados: ArrayList<Int> = ArrayList()

    var modoEdit = false
    private var adapterList = addEjercAdapter()
    private val recyclerAdapter = RecyclerAdapterEjerciciosEntrenamientos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.entrenamiento = intent.getSerializableExtra("entrenamiento") as Entrenamiento
        setTitle(entrenamiento.nombre)
        cargarDatos()
        val type = arrayOf("Resistencia", "Potencia", "Flexibilidad", "Velocidad", "Hipertrofia")
        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, type)
        binding.ettipoEntren.setAdapter(adapter)



        adapterList.adapterList(this, ejerciciosSelect)
        binding.lvExercise.adapter = adapterList


        cargarEjercicios()
        setUpRecyclerView()
        loadRecyclerViewAdapter()

        binding.btnAddEjerc.setOnClickListener {
            if (ejercicios.size > 0){
                mostrarDialog()
            }
        }

        binding.btnGuardar.setOnClickListener {
            if (comprobarCamposObligatorios()){
                guardarEntrenamiento()
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

    private fun guardarEntrenamiento(){

        binding.lyProgress.isVisible = true
        binding.btnGuardar.isVisible = false

        entrenamiento.nombre = binding.etNombreEntren.text.toString()
        entrenamiento.tipo = binding.ettipoEntren.text.toString()
        entrenamiento.descripcion = if (TextUtils.isEmpty(binding.etDescrip.text.toString())) "" else binding.etDescrip.text.toString()
        entrenamiento.series = binding.etSeries.text.toString().toInt()
        entrenamiento.repeticiones = binding.etRep.text.toString().toInt()
        entrenamiento.descanso = if (TextUtils.isEmpty(binding.etDesc.text.toString())) 0 else binding.etDesc.text.toString().toInt()
        entrenamiento.ejercicios.clear()
        ejerciciosSelect.forEach { e -> entrenamiento.ejercicios.add(e.id) }

        db.collection("entrenamientos").document(entrenamiento.id).set(entrenamiento).addOnSuccessListener {
            Toast.makeText(this, "Entrenamiento actualizado con éxito", Toast.LENGTH_LONG).show()
            setTitle(entrenamiento.nombre)
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            noMostrar()
        }.addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al actualizar el entrenamiento")
        }
    }

    private fun cargarEjercicios(){
        db.collection("users").document(MainActivity.entrenador.email).get().addOnSuccessListener {
                doc ->
            var idEjercicios: MutableList<String> = arrayListOf()
            if (doc.get("ejercicios") != null){
                idEjercicios = doc.get("ejercicios") as MutableList<String>
            }

            for (id in idEjercicios){

                db.collection("ejercicios").document(id).get().addOnSuccessListener {
                        ejerc ->
                    ejerc.toObject(Ejercicio::class.java)?.let { it1 -> ejercicios.add(it1) }
                }
            }
        }
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

    private fun mostrarDialog(){
        itemsSeleccionados.clear()
        var items = arrayOfNulls<CharSequence>(ejercicios.size)
        for (i in 0 until ejercicios.size){
            items[i] = ejercicios[i].nombre
        }
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Ejercicios")
            .setMultiChoiceItems(items, null,
                DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
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
                                    ejerciciosSelect.add(ejerc)
                                    adapterList.adapterList(this, ejerciciosSelect)
                                    adapterList.notifyDataSetChanged()
                                    recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
                                    recyclerAdapter.notifyDataSetChanged()
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val ejerc = doc.documents[0].toObject(
                                        Ejercicio::class.java)
                                    for (i in 0 until ejerciciosEntrenamiento.size){
                                        if (ejerciciosEntrenamiento[i].id == ejerc!!.id){
                                            ejerciciosEntrenamiento.set(i,ejerc)
                                            ejerciciosSelect.set(i,ejerc)
                                            adapterList.adapterList(this, ejerciciosSelect)
                                            adapterList.notifyDataSetChanged()
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
        binding.etNombreEntren.setText(entrenamiento.nombre)
        val tipo = "Entrenamiento de ${entrenamiento.tipo}"
        binding.etTipoVista.setText(tipo)
        binding.ettipoEntren.setText(entrenamiento.tipo)

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

    private fun noMostrar(){
        modoEdit = false
        binding.tlNombreEntren.isVisible = false
        binding.tlTipoEntren.isVisible = false
        binding.tlTipoVista.isVisible = true
        val tipo = "Entrenamiento de ${entrenamiento.tipo}"
        binding.etTipoVista.setText(tipo)

        binding.etDescrip.clearFocus()
        binding.etDescrip.isFocusableInTouchMode = false
        binding.etSeries.isFocusableInTouchMode = false
        binding.etRep.isFocusableInTouchMode = false

        binding.etDesc.isFocusableInTouchMode = false
        binding.btnGuardar.isVisible = false
        binding.btnAddEjerc.isVisible = false
        binding.lvExercise.isVisible = false
        ejerciciosEntrenamiento = ejerciciosSelect
        recyclerAdapter.RecyclerAdapter(ejerciciosEntrenamiento, this)
        recyclerAdapter.notifyDataSetChanged()
        binding.rvExercise.isVisible = true

    }

    private fun mostrarTodo(){

        modoEdit = true
        binding.tlNombreEntren.isVisible = true
        binding.tlTipoEntren.isVisible = true
        binding.tlTipoVista.isVisible = false
        binding.tlDescripc.isVisible = true
        binding.etDescrip.isFocusableInTouchMode = true
        binding.etSeries.isFocusableInTouchMode = true
        binding.etRep.isFocusableInTouchMode = true
        binding.tlDesc.isVisible = true
        binding.etDesc.isFocusableInTouchMode = true
        binding.btnGuardar.isVisible = true
        binding.btnAddEjerc.isVisible = true
        binding.lvExercise.isVisible = true
        binding.rvExercise.isVisible = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.edit_ejercicio_menu, menu)
        return true
    }

    private fun volver_Pagina(){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setTitle("Volver a la página anterior")
            setMessage("Perderás todos los datos no guardados, ¿estás seguro?")
            setPositiveButton(
                android.R.string.ok){_,_->super.onBackPressed()}
            setNegativeButton(
                android.R.string.cancel){_,_->}
        }
        builder.show()
    }

    override fun onBackPressed() {
        if (modoEdit){
            volver_Pagina()
        }else{
            super.onBackPressed()
        }
    }

    fun delete(){
        db.collection("users").document(MainActivity.entrenador.email).get().addOnSuccessListener{doc->

            var entrenamientos:MutableList<String> = arrayListOf()

            if (doc.get("entrenamientos") != null){
                entrenamientos = doc.get("entrenamientos") as MutableList<String>
                entrenamientos.remove(entrenamiento.id)
            }

            db.collection("users").document(MainActivity.entrenador.email)
                .update("entrenamientos", entrenamientos).addOnSuccessListener{
                    Toast.makeText(this, "El entrenamiento ha sido eliminado con éxito", Toast.LENGTH_LONG).show()

                    onBackPressed()
                }.addOnFailureListener {
                    Functions().showSnackSimple(binding.root,"Ha habido un error al eliminar el entrenamiento")
                }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                if (modoEdit){
                    volver_Pagina()
                } else {
                    onBackPressed()
                }
                true
            }

            R.id.edit_ejerc -> {
                mostrarTodo()

                return true
            }

            R.id.delete_ejerc -> {
                val builder = AlertDialog.Builder(this)

                builder.apply {
                    setTitle("Eliminar entrenamiento")
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