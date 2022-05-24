package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentHomeBinding
import android.view.MenuInflater
import android.view.MenuItem.OnActionExpandListener
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.esentrenador
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.searchView
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.calendario.CalendarioActivity
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.lang.Exception
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: EntFragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val recyclerAdapterDeportista = RecyclerAdapterHomeDeportista()
    private val recyclerAdapterEntrenador = RecyclerAdapterHomeEntrenador()

    private lateinit var search:MenuItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = EntFragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)
        binding.btnCompletCalendar.setOnClickListener {
            val myIntent = Intent (context, CalendarioActivity::class.java)
            startActivity(myIntent)
        }

        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()
        // Add the new menu items
        inflater.inflate(R.menu.main, menu)

        search = menu.findItem(R.id.app_bar_search)

        searchView = search.actionView as SearchView
        searchView.queryHint = "Buscar"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {

                if (esentrenador){
                    recyclerAdapterEntrenador.filter(text!!)
                } else {
                    recyclerAdapterDeportista.filter(text!!)
                }

                return true
            }

        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRV.isVisible = true
        binding.tvInfoRV.text = "Semana sin entrenamientos programados"

        if (homeViewModel.entrenamientos.size > 0){

            binding.tvInfoRV.isVisible = false

            binding.rvhome.setHasFixedSize(true)
            binding.rvhome.layoutManager = LinearLayoutManager(requireContext())


            recyclerAdapterDeportista.RecyclerAdapter(homeViewModel.entrenamientos, requireContext())
            binding.rvhome.adapter = recyclerAdapterDeportista
        }

    }

    private fun setUpRecyclerViewEntrenador() {

        binding.tvInfoRV.isVisible = true

        //Si el listado tiene algún dato se quitará el textview y se cargará el adapter en el recyclerview
        if (homeViewModel.entrenamientos.size > 0) {

            binding.tvInfoRV.isVisible = false

            binding.rvhome.setHasFixedSize(true)
            binding.rvhome.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapterEntrenador.RecyclerAdapter(homeViewModel.entrenamientos, requireContext())
            binding.rvhome.adapter = recyclerAdapterEntrenador
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onStart() {
        super.onStart()
        if (esentrenador){
            binding.textHome.text = context!!.getString(R.string.tvFechaActual, Functions().mostrarFecha())
            if (!homeViewModel.recyclercargado){
                loadRecyclerViewAdapterEntrenador()
            }
            setUpRecyclerViewEntrenador()
            binding.btnCompletCalendar.isVisible = true
        } else {

            binding.textHome.isVisible = false
            if (!homeViewModel.recyclercargado){
                loadRecyclerViewAdapterDeportista()
            }
            setUpRecyclerView()
            binding.btnCompletCalendar.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (esentrenador){
            binding.textHome.text = context!!.getString(R.string.tvFechaActual, Functions().mostrarFecha())
            if (!homeViewModel.recyclercargado){
                loadRecyclerViewAdapterEntrenador()
            }
            setUpRecyclerViewEntrenador()
            binding.btnCompletCalendar.isVisible = true
        } else {
            binding.textHome.isVisible = false
            if (!homeViewModel.recyclercargado){
                loadRecyclerViewAdapterDeportista()
            }
            setUpRecyclerView()
            binding.btnCompletCalendar.isVisible = false
        }
    }

    private fun mostrarEntrenoDeportista(deportistaDB: DeportistaDB?){
        if (deportistaDB!!.entrenamientos != null) {
            val ultimoDiaSemana = Functions().ultimoDiaSemana()
            var posicion = 0
            for (entre: Entrenamiento_DeportistaDB in deportistaDB.entrenamientos!!){

                val entreno = Entrenamiento_Deportista()
                entreno.posicion = posicion
                entreno.prueba = entre.prueba
                posicion++
                entreno.deportista = deportistaDB
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                entreno.fechaFormat = sdf.parse(entre.fecha)

                if (Functions().calcularFecha(entre.fecha) == 0) {
                    entreno.fecha = "Hoy - ${entre.fecha}"
                } else if (Functions().calcularFecha(entre.fecha) < 0 && Functions().calcularEntreFechas(ultimoDiaSemana,entre.fecha) >= 0) {
                    entreno.fecha = Functions().diaSemana(entre.fecha, requireContext())
                } else {
                    continue
                }

                entreno.realizado = entre.realizado

                db.collection("entrenamientos").whereEqualTo(FieldPath.documentId(),entre.entrenamiento)
                    .addSnapshotListener{doc, exc ->
                        if (exc != null){
                            Log.w(ContentValues.TAG, "Listen failed.", exc)
                            return@addSnapshotListener
                        }

                        if (doc != null){
                            for (dc in doc.documentChanges){
                                when (dc.type){
                                    DocumentChange.Type.ADDED -> {
                                        entreno.entrenamiento = doc.documents[0].toObject(
                                            Entrenamiento::class.java)!!
                                        homeViewModel.entrenamientos.add(entreno)
                                        homeViewModel.entrenamientos.sortBy { e -> e.fechaFormat }
                                        if (_binding != null){
                                            setUpRecyclerView()
                                            recyclerAdapterDeportista.notifyDataSetChanged()
                                        }
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        entreno.entrenamiento = doc.documents[0].toObject(
                                            Entrenamiento::class.java)!!
                                        for (i in 0 until homeViewModel.entrenamientos.size){
                                            if (homeViewModel.entrenamientos[i].entrenamiento.id == entreno.entrenamiento.id){
                                                homeViewModel.entrenamientos.set(i,entreno)
                                                homeViewModel.entrenamientos.sortBy { e -> e.fechaFormat }
                                            }
                                        }
                                        if (_binding != null){
                                            setUpRecyclerView()
                                            recyclerAdapterDeportista.notifyDataSetChanged()
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }

                    }

            }
        }
    }

    private fun loadRecyclerViewAdapterDeportista(){
        try {
            homeViewModel.recyclercargado = true
            db.collection("users").whereEqualTo(FieldPath.documentId(), deportistaMain.email)
                .addSnapshotListener{ doc, exc ->
                    if (exc != null){
                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                        return@addSnapshotListener
                    }

                    if (doc != null){

                        for (dc in doc.documentChanges){
                            when (dc.type){
                                DocumentChange.Type.ADDED -> {
                                    val deportistaDB = dc.document.toObject(DeportistaDB::class.java)
                                    mostrarEntrenoDeportista(deportistaDB)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val deportistaDB = dc.document.toObject(DeportistaDB::class.java)
                                    homeViewModel.entrenamientos.clear()
                                    mostrarEntrenoDeportista(deportistaDB)
                                }
                            }
                        }
                    }
                }
        } catch (e:Exception){
            e.message?.let { Log.d("HOME", it) }
        }

    }

     fun loadRecyclerViewAdapterEntrenador() {
        homeViewModel.recyclercargado = true
        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").whereEqualTo(FieldPath.documentId(),current)
            .addSnapshotListener { doc, exc ->
                if (exc != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null) {
                    for (d in doc.documentChanges){
                        when (d.type){
                            DocumentChange.Type.ADDED -> {
                                val entrenadorDB = d.document.toObject(EntrenadorDB::class.java)
                                homeViewModel.entrenamientos.clear()
                                for (deportista in entrenadorDB.deportistas) {
                                    db.collection("users").whereEqualTo(FieldPath.documentId(),deportista)
                                        .addSnapshotListener { document, except ->
                                            if (except != null) {
                                                Log.w(ContentValues.TAG, "Listen failed.", except)
                                                return@addSnapshotListener
                                            }

                                            if (document != null) {

                                                for (dc in document.documentChanges) {
                                                    when (dc.type) {
                                                        DocumentChange.Type.ADDED -> {
                                                            val deportistaDB = document.documents[0].toObject(DeportistaDB::class.java)
                                                            if (deportistaDB != null) {
                                                                mostrarentrenos(deportistaDB)
                                                            }
                                                        }
                                                        DocumentChange.Type.MODIFIED -> {
                                                            val deportistaDB = document.documents[0].toObject(DeportistaDB::class.java)
                                                            val copy:MutableList<Entrenamiento_Deportista> = arrayListOf()
                                                            for(i in 0 until homeViewModel.entrenamientos.size){
                                                                if (deportistaDB != null) {
                                                                    if (homeViewModel.entrenamientos[i].deportista.email != deportistaDB.email){
                                                                        copy.add(homeViewModel.entrenamientos[i])
                                                                    }
                                                                }
                                                            }
                                                            homeViewModel.entrenamientos.clear()
                                                            homeViewModel.entrenamientos.addAll(copy)

                                                            if (deportistaDB != null) {
                                                                modificaentrenos(deportistaDB)
                                                            }
                                                        }
                                                        else -> {}
                                                    }
                                                }


                                            }
                                        }
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val entrenadorDB = d.document.toObject(EntrenadorDB::class.java)
                                val deportistas:MutableList<String> = arrayListOf()

                                for (deportista in entrenadorDB.deportistas){
                                    var aparece = false
                                    for (e in homeViewModel.entrenamientos){
                                        if (e.deportista.email == deportista){
                                            aparece = true
                                        }
                                    }

                                    if (!aparece) deportistas.add(deportista)
                                }

                                for (deportista in deportistas) {
                                    db.collection("users").whereEqualTo(FieldPath.documentId(),deportista)
                                        .addSnapshotListener { document, except ->
                                            if (except != null) {
                                                Log.w(ContentValues.TAG, "Listen failed.", except)
                                                return@addSnapshotListener
                                            }

                                            if (document != null) {

                                                for (dc in document.documentChanges) {
                                                    when (dc.type) {
                                                        DocumentChange.Type.ADDED -> {
                                                            val deportistaDB = document.documents[0].toObject(DeportistaDB::class.java)
                                                            if (deportistaDB != null) {
                                                                mostrarentrenos(deportistaDB)
                                                            }
                                                        }
                                                        DocumentChange.Type.MODIFIED -> {
                                                            val deportistaDB = document.documents[0].toObject(DeportistaDB::class.java)
                                                            val copy:MutableList<Entrenamiento_Deportista> = arrayListOf()
                                                            for(i in 0 until homeViewModel.entrenamientos.size){
                                                                if (deportistaDB != null) {
                                                                    if (homeViewModel.entrenamientos[i].deportista.email != deportistaDB.email){
                                                                        copy.add(homeViewModel.entrenamientos[i])
                                                                    }
                                                                }
                                                            }
                                                            homeViewModel.entrenamientos.clear()
                                                            homeViewModel.entrenamientos.addAll(copy)

                                                            if (deportistaDB != null) {
                                                                modificaentrenos(deportistaDB)
                                                            }
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

    private fun modificaentrenos(deportistaDB: DeportistaDB){
        if (deportistaDB.entrenamientos != null) {
            var posicion = 0
            for (entre in deportistaDB.entrenamientos!!) {
                val entreno = Entrenamiento_Deportista()
                entreno.posicion = posicion
                entreno.prueba = entre.prueba
                posicion++
                entreno.deportista = deportistaDB
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                entreno.fechaFormat = sdf.parse(entre.fecha)

                //Cogemos solo la fecha de hoy
                if (Functions().calcularFecha(entre.fecha) == 0) {
                    entreno.fecha = "Hoy - ${entre.fecha}"
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
                                    homeViewModel.entrenamientos.add(entreno)
                                    homeViewModel.entrenamientos.sortByDescending { e -> e.realizado }
                                    if (_binding != null){
                                        setUpRecyclerViewEntrenador()
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                }
            }
        }
    }


    private fun mostrarentrenos(deportistaDB: DeportistaDB){
        if (deportistaDB.entrenamientos != null) {

            var posicion = 0
            for (entre in deportistaDB.entrenamientos!!) {
                val entreno = Entrenamiento_Deportista()
                entreno.posicion = posicion
                entreno.prueba = entre.prueba
                posicion++
                entreno.deportista = deportistaDB
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                entreno.fechaFormat = sdf.parse(entre.fecha)

                //Cogemos solo la fecha de hoy
                if (Functions().calcularFecha(entre.fecha) == 0) {
                    entreno.fecha = "Hoy - ${entre.fecha}"
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

                                    homeViewModel.entrenamientos.add(entreno)
                                    homeViewModel.entrenamientos.sortByDescending { e -> e.realizado }
                                    if (_binding != null){
                                        setUpRecyclerViewEntrenador()
                                        binding.tvInfoRV.isVisible = false
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.app_bar_search -> {

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}