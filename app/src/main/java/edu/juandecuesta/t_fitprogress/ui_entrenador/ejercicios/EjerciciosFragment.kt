package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEjerciciosBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db

class EjerciciosFragment : Fragment() {

    private lateinit var ejerciciosViewModel: EjerciciosViewModel
    private var _binding: EntFragmentEjerciciosBinding? = null

    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapterEjerciciosEntrenador()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ejerciciosViewModel =
            ViewModelProvider(this).get(EjerciciosViewModel::class.java)

        _binding = EntFragmentEjerciciosBinding.inflate(inflater, container, false)

        val root: View = binding.root

        ejerciciosViewModel.ejercicios.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()
        setHasOptionsMenu(true)

        binding.tbnAddEjerc.setOnClickListener {

            val myIntent = Intent (context, EjercicioActivity::class.java)
            startActivity(myIntent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()

        // Add the new menu items
        inflater.inflate(R.menu.main, menu)

        val search = menu?.findItem(R.id.app_bar_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar ejercicio"

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


    private fun setUpRecyclerView() {

        binding.tvSinEjercicios.isVisible = true

        if (MainActivity.entrenadorMain.ejercicios.size > 0){

            binding.tvSinEjercicios.isVisible = false

            binding.rvEjercicios.setHasFixedSize(true)
            binding.rvEjercicios.layoutManager = GridLayoutManager(requireContext(),2)


            recyclerAdapter.RecyclerAdapter(ejerciciosViewModel.ejercicios, requireContext())
            binding.rvEjercicios.adapter = recyclerAdapter
        }

    }

    private fun loadRecyclerViewAdapter(){

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").document(current)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                    binding.tvSinEjercicios.isVisible = true
                    ejerciciosViewModel.ejercicios.clear()
                    recyclerAdapter.RecyclerAdapter(ejerciciosViewModel.ejercicios, requireContext())
                    recyclerAdapter.notifyDataSetChanged()

                    for (idEjerc:String in entrenadorDb?.ejercicios!!){
                        binding.tvSinEjercicios.isVisible = false

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
                                                ejerciciosViewModel.ejercicios.add(ejerc!!)
                                                recyclerAdapter.RecyclerAdapter(ejerciciosViewModel.ejercicios, requireContext())
                                                recyclerAdapter.notifyDataSetChanged()
                                            }
                                            DocumentChange.Type.MODIFIED -> {
                                                val ejerc = doc.documents[0].toObject(
                                                    Ejercicio::class.java)
                                                for (i in 0 until ejerciciosViewModel.ejercicios.size){
                                                    if (ejerciciosViewModel.ejercicios[i].id == ejerc!!.id){
                                                        ejerciciosViewModel.ejercicios.set(i,ejerc)
                                                        recyclerAdapter.RecyclerAdapter(ejerciciosViewModel.ejercicios, requireContext())
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
            }
    }

}