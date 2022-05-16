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
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEntrenamientosBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.MainActivity

class EntrenamientosFragment:Fragment() {
    private lateinit var entrenamientosViewModel: EntrenamientosViewModel
    private var _binding: EntFragmentEntrenamientosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapterEntrenamientos()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
        val searchView = search?.actionView as SearchView
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

    private fun loadRecyclerViewAdapter(){

        try {
            val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
            db.collection("users").document(current)
                .addSnapshotListener{ doc, exc ->
                    if (exc != null){
                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                        return@addSnapshotListener
                    }

                    if (doc != null){
                        val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                        binding.tvSinEntrenam.isVisible = true
                        entrenamientosViewModel.entrenamientos.clear()
                        recyclerAdapter.RecyclerAdapter(entrenamientosViewModel.entrenamientos, requireContext())
                        recyclerAdapter.notifyDataSetChanged()

                        for (idEntren:String in entrenadorDb?.entrenamientos!!){
                            binding.tvSinEntrenam.isVisible = false

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
                                                    val entren = doc.documents[0].toObject(
                                                        Entrenamiento::class.java)
                                                    entrenamientosViewModel.entrenamientos.add(entren!!)
                                                    recyclerAdapter.RecyclerAdapter(entrenamientosViewModel.entrenamientos, requireContext())
                                                    recyclerAdapter.notifyDataSetChanged()
                                                }
                                                DocumentChange.Type.MODIFIED -> {
                                                    val entren = doc.documents[0].toObject(
                                                        Entrenamiento::class.java)
                                                    for (i in 0 until entrenamientosViewModel.entrenamientos.size){
                                                        if (entrenamientosViewModel.entrenamientos[i].id == entren!!.id){
                                                            entrenamientosViewModel.entrenamientos.set(i,entren)
                                                            recyclerAdapter.RecyclerAdapter(entrenamientosViewModel.entrenamientos, requireContext())
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
        }catch (e:Exception){
            e.message?.let { Log.d("ENTRENAMIENTOS", it) }
        }
    }


}