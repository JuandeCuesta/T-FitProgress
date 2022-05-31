package edu.juandecuesta.t_fitprogress.ui_entrenador.calendario

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import edu.juandecuesta.t_fitprogress.databinding.ActivityCalendarioBinding
import java.util.*
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.home.RecyclerAdapterHomeEntrenador
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat


class CalendarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarioBinding

    private val mEventDays: MutableList<EventDay> = ArrayList()
    private var entrenamientos: MutableList<Entrenamiento_Deportista> = arrayListOf()
    private val recyclerAdapter = RecyclerAdapterHomeEntrenador()
    var posicion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(year,month,day)
        binding.calendarView.setDate(calendar)

        cargarDatos()

        binding.calendarView.setOnDayClickListener {
            val yearS = it.calendar.get(Calendar.YEAR)
            val monthS = it.calendar.get(Calendar.MONTH)
            val dayS = it.calendar.get(Calendar.DAY_OF_MONTH)
            val fecha = ("$dayS/${monthS+1}/$yearS")
            binding.txtFechaSelect.text = fecha
            binding.txtFechaSelect.isVisible = true
            binding.tvInfoRV.isVisible = true
            cargarfecha(fecha)
        }
    }

    fun cargarfecha(fecha: String){
        var copy:MutableList<Entrenamiento_Deportista> = arrayListOf()
        copy.addAll(entrenamientos)
        copy = copy.filter { e -> Functions().calcularEntreFechas(fecha,e.fecha) == 0 } as MutableList<Entrenamiento_Deportista>
        recyclerAdapter.RecyclerAdapter(copy, this)
        recyclerAdapter.notifyDataSetChanged()
        setUpRecyclerView(copy)
    }

    override fun onResume() {
        super.onResume()
        if (binding.txtFechaSelect.text.toString() != ""){
            cargarfecha(binding.txtFechaSelect.text.toString())
        }
    }

    private fun cargarDatos() {
        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").document(current)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                    mEventDays.clear()
                    for (emailDep:String in entrenadorDb?.deportistas!!){

                        db.collection("users").whereEqualTo(FieldPath.documentId(),emailDep)
                            .addSnapshotListener{doc, exc ->
                                if (exc != null){
                                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                                    return@addSnapshotListener
                                }

                                if (doc != null){
                                    for (dc in doc.documentChanges){
                                        when (dc.type){
                                            DocumentChange.Type.ADDED -> {
                                                val deportistaDB = doc.documents[0].toObject(
                                                    DeportistaDB::class.java)
                                                posicion = 0
                                                if (deportistaDB?.entrenamientos != null){
                                                    for (e in deportistaDB.entrenamientos!!){
                                                        addeventday(e.fecha)
                                                        mostrarentreno(deportistaDB, e)
                                                    }
                                                }

                                            }
                                            DocumentChange.Type.MODIFIED -> {
                                                val deportistaDB = doc.documents[0].toObject(
                                                    DeportistaDB::class.java)
                                                if (deportistaDB?.entrenamientos != null){
                                                    val copy:MutableList<Entrenamiento_Deportista> = arrayListOf()
                                                    for (i in 0 until entrenamientos.size){
                                                        if (entrenamientos[i].deportista.email != deportistaDB.email){
                                                            copy.add(entrenamientos[i])
                                                        }
                                                    }
                                                    entrenamientos.clear()
                                                    entrenamientos.addAll(copy)
                                                    mEventDays.clear()
                                                    for (ent in entrenamientos){
                                                        addeventday(ent.fecha)
                                                    }
                                                    posicion = 0
                                                    for (e in deportistaDB.entrenamientos!!){
                                                        addeventday(e.fecha)
                                                        mostrarentreno(deportistaDB, e)
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                            }
                    }

                }
            }
    }


    fun addeventday (fecha: String){
        val calendar = Calendar.getInstance()
        val day = fecha.split("/")[0].toInt()
        val month = (fecha.split("/")[1].toInt() - 1)
        val year = fecha.split("/")[2].toInt()
        calendar.set(year,month,day)
        val eventDay = EventDay(calendar, R.drawable.icon_entrenamiento_black)
        mEventDays.add(eventDay)
        binding.calendarView.setEvents(mEventDays)
    }

    fun mostrarentreno (deportistaDB: DeportistaDB, entre: Entrenamiento_DeportistaDB){

        val entreno = Entrenamiento_Deportista()
        entreno.posicion = posicion
        entreno.prueba = entre.prueba
        posicion++
        entreno.deportista = deportistaDB
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        entreno.fechaFormat = sdf.parse(entre.fecha)
        entreno.fecha = entre.fecha

        entreno.realizado = entre.realizado

        db.collection("entrenamientos").whereEqualTo(
            FieldPath.documentId(),
            entre.entrenamiento
        ).addSnapshotListener { documento, excepcion ->
            if (excepcion != null) {
                Log.w(
                    ContentValues.TAG,
                    "Listen failed.",
                    excepcion
                )
                return@addSnapshotListener
            }

            if (documento != null) {

                for (dc in documento.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            entreno.entrenamiento = documento.documents[0].toObject(
                                Entrenamiento::class.java)!!
                            entrenamientos.add(entreno)

                            if (!TextUtils.isEmpty(binding.txtFechaSelect.text)){
                                cargarfecha(binding.txtFechaSelect.text.toString())
                            }

                        }
                        DocumentChange.Type.MODIFIED -> {
                            entreno.entrenamiento = documento.documents[0].toObject(
                                Entrenamiento::class.java)!!

                            for (i in 0 until entrenamientos.size){
                                if (entrenamientos[i].entrenamiento.id == entreno.entrenamiento.id){
                                    entrenamientos.set(i, entreno)
                                }
                            }

                            if (!TextUtils.isEmpty(binding.txtFechaSelect.text)){
                                cargarfecha(binding.txtFechaSelect.text.toString())
                            }
                        }
                        else -> {}
                    }
                }
            }

        }
    }

    private fun setUpRecyclerView(copy:MutableList<Entrenamiento_Deportista>) {

        binding.tvInfoRV.isVisible = true
        binding.rvEntrenamientoFecha.isVisible = false

        //Si el listado tiene algún dato se quitará el textview y se cargará el adapter en el recyclerview
        if (copy.size > 0) {

            binding.tvInfoRV.isVisible = false
            binding.rvEntrenamientoFecha.isVisible = true

            binding.rvEntrenamientoFecha.setHasFixedSize(true)
            binding.rvEntrenamientoFecha.layoutManager = LinearLayoutManager(this)

            recyclerAdapter.RecyclerAdapter(copy, this)
            binding.rvEntrenamientoFecha.adapter = recyclerAdapter
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