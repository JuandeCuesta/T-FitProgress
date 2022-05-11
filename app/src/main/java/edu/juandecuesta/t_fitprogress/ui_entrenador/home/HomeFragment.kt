package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import android.content.ContentValues
import android.content.Intent
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
import android.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.ui_entrenador.calendario.CalendarioActivity
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: EntFragmentHomeBinding


    private val recyclerAdapter = RecyclerAdapterHomeEntrenador()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = EntFragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textHome.text = context!!.getString(R.string.tvFechaActual, Functions().mostrarFecha())
        loadRecyclerViewAdapter()

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

        val search = menu?.findItem(R.id.app_bar_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                recyclerAdapter.filter(text!!)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRV.isVisible = true

        //Si el listado tiene algún dato se quitará el textview y se cargará el adapter en el recyclerview
        if (homeViewModel.entrenamientos.size > 0) {

            binding.tvInfoRV.isVisible = false

            binding.rvhome.setHasFixedSize(true)
            binding.rvhome.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapter.RecyclerAdapter(homeViewModel.entrenamientos, requireContext())
            binding.rvhome.adapter = recyclerAdapter
        }
    }

    private fun loadRecyclerViewAdapter() {

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
                                        val deportistaDB =
                                            document.toObject(DeportistaDB::class.java)
                                        binding.tvInfoRV.isVisible = true
                                        homeViewModel.entrenamientos.clear()
                                        recyclerAdapter.RecyclerAdapter(
                                            homeViewModel.entrenamientos,
                                            requireContext()
                                        )
                                        recyclerAdapter.notifyDataSetChanged()

                                        if (deportistaDB!!.entrenamientos != null) {
                                            for (entre in deportistaDB.entrenamientos!!) {

                                                val entreno = Entrenamiento_Deportista()
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
                                                                        entreno.entrenamiento = documento.documents[0].toObject(Entrenamiento::class.java)
                                                                        homeViewModel.entrenamientos.add(entreno)
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
        return when(item.itemId){

            R.id.app_bar_search -> {

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}