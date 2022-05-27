package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEntrenamientosBinding
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.searchView

class EntrenamientosFragment:Fragment() {
    private lateinit var entrenamientosViewModel: EntrenamientosViewModel
    private var _binding: EntFragmentEntrenamientosBinding? = null

    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapterEntrenamientos()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        entrenamientosViewModel =
            ViewModelProvider(this).get(EntrenamientosViewModel::class.java)

        _binding = EntFragmentEntrenamientosBinding.inflate(inflater, container, false)

        val root: View = binding.root

        entrenamientosViewModel.entrenamientos.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()
        setHasOptionsMenu(true)

        binding.btnAddEntrenamiento.setOnClickListener {

            val myIntent = Intent (context, CreateEntrenamientoActivity::class.java)
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
        searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar entrenamiento"

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

        binding.tvSinEntrenam.isVisible = true

        if (MainActivity.entrenadorMain.entrenamientos.size > 0){

            binding.tvSinEntrenam.isVisible = false

            binding.rvEntrenamiento.setHasFixedSize(true)
            binding.rvEntrenamiento.layoutManager = LinearLayoutManager(requireContext())


            recyclerAdapter.RecyclerAdapter(entrenamientosViewModel.entrenamientos, requireContext())
            binding.rvEntrenamiento.adapter = recyclerAdapter
        }

    }

    private fun mostrarentrenos (idEntren:String){
        if (_binding != null){
            binding.tvSinEntrenam.isVisible = false
        }

        db.collection("entrenamientos").whereEqualTo(FieldPath.documentId(),idEntren)
            .addSnapshotListener{doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){

                    for (dc in doc.documentChanges){
                        when (dc.type){
                            DocumentChange.Type.ADDED -> {
                                if (_binding != null){
                                    val entren = doc.documents[0].toObject(
                                        Entrenamiento::class.java)
                                    entrenamientosViewModel.entrenamientos.add(entren!!)
                                    setUpRecyclerView()
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                if (_binding != null){
                                    val entren = doc.documents[0].toObject(
                                        Entrenamiento::class.java)
                                    for (i in 0 until entrenamientosViewModel.entrenamientos.size){
                                        if (entrenamientosViewModel.entrenamientos[i].id == entren!!.id){
                                            entrenamientosViewModel.entrenamientos.set(i,entren)
                                        }
                                    }
                                    if (_binding != null){
                                        setUpRecyclerView()
                                    }

                                }

                            }
                        }
                    }
                }

            }
    }

    private fun loadRecyclerViewAdapter(){

        try {
            val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
            db.collection("users").whereEqualTo(FieldPath.documentId(), current)
                .addSnapshotListener{ doc, exc ->
                    if (exc != null){
                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                        return@addSnapshotListener
                    }

                    if (doc != null){

                        for (dc in doc.documentChanges){
                            when (dc.type){
                                DocumentChange.Type.ADDED -> {
                                    val entrenadorDb = dc.document.toObject(EntrenadorDB::class.java)
                                    for (idEntren:String in entrenadorDb?.entrenamientos!!){
                                        mostrarentrenos(idEntren)
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val entrenadorDb = dc.document.toObject(EntrenadorDB::class.java)
                                    entrenamientosViewModel.entrenamientos.clear()
                                    if (_binding != null){
                                        recyclerAdapter.RecyclerAdapter(entrenamientosViewModel.entrenamientos, requireContext())
                                        recyclerAdapter.notifyDataSetChanged()
                                    }
                                    for (idEntren:String in entrenadorDb?.entrenamientos!!){
                                        mostrarentrenos(idEntren)
                                    }
                                }
                            }
                        }

                    }
                }
        }catch (e:Exception){
            e.message?.let { Log.d("ENTRENAMIENTOS", it) }
        }
    }


}