package edu.juandecuesta.t_fitprogress.ui_entrenador.calendario

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.home.RecyclerAdapterHomeEntrenador
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat


class CalendarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarioBinding

    private val mEventDays: MutableList<EventDay> = ArrayList()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var entrenamientos: MutableList<Entrenamiento_Deportista> = arrayListOf()
    private val recyclerAdapter = RecyclerAdapterHomeEntrenador()

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
            entrenamientos.clear()
            recyclerAdapter.notifyDataSetChanged()

            loadRecyclerViewAdapter(fecha)
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
                                                val deportistaDB = doc.documents[0].toObject(DeportistaDB::class.java)
                                                if (deportistaDB?.entrenamientos != null){
                                                    for (e in deportistaDB.entrenamientos!!){
                                                        val calendar = Calendar.getInstance()
                                                        val day = e.fecha.split("/")[0].toInt()
                                                        val month = (e.fecha.split("/")[1].toInt() - 1)
                                                        val year = e.fecha.split("/")[2].toInt()
                                                        calendar.set(year,month,day)
                                                        val eventDay = EventDay(calendar, R.drawable.icon_entrenamiento_black)
                                                        mEventDays.add(eventDay)
                                                        binding.calendarView.setEvents(mEventDays)
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


    private fun setUpRecyclerView() {

        binding.tvInfoRV.isVisible = true

        //Si el listado tiene algún dato se quitará el textview y se cargará el adapter en el recyclerview
        if (entrenamientos.size > 0) {

            binding.tvInfoRV.isVisible = false

            binding.rvEntrenamientoFecha.setHasFixedSize(true)
            binding.rvEntrenamientoFecha.layoutManager = LinearLayoutManager(this)

            recyclerAdapter.RecyclerAdapter(entrenamientos, this)
            binding.rvEntrenamientoFecha.adapter = recyclerAdapter
        }
    }

    private fun loadRecyclerViewAdapter(fecha:String) {

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").document(current)
            .addSnapshotListener { doc, exc ->
                if (exc != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null) {
                    val entrenadorDB = doc.toObject(EntrenadorDB::class.java)

                    if (entrenadorDB!!.deportistas != null) {
                        for (deportista in entrenadorDB.deportistas) {

                            db.collection("users").document(deportista)
                                .addSnapshotListener { document, except ->
                                    if (except != null) {
                                        Log.w(ContentValues.TAG, "Listen failed.", except)
                                        return@addSnapshotListener
                                    }

                                    if (document != null) {
                                        val deportistaDB = document.toObject(DeportistaDB::class.java)

                                        if (deportistaDB!!.entrenamientos != null) {
                                            for (entre in deportistaDB.entrenamientos!!) {

                                                val entreno = Entrenamiento_Deportista()
                                                entreno.deportista = deportistaDB
                                                val sdf = SimpleDateFormat("dd/MM/yyyy")
                                                entreno.fechaFormat = sdf.parse(entre.fecha)

                                                //Cogemos solo la fecha seleccionada
                                                if (Functions().calcularEntreFechas(fecha,entre.fecha) == 0) {
                                                    entreno.fecha = entre.fecha
                                                } else {
                                                    continue
                                                }

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
                                                                    entreno.entrenamiento =
                                                                        documento.documents[0].toObject(Entrenamiento::class.java)!!
                                                                    entrenamientos.add(entreno)
                                                                    setUpRecyclerView()
                                                                    recyclerAdapter.notifyDataSetChanged()

                                                                    binding.tvInfoRV.isVisible = false
                                                                }
                                                                else -> {}
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