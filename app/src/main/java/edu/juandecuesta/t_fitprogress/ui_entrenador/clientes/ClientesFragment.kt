package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

import android.content.ContentValues.TAG
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
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.entrenadorMain
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEjerciciosBinding


class ClientesFragment : Fragment() {

    private lateinit var clientesViewModel: ClientesViewModel
    private var _binding: EntFragmentClientesBinding? = null

    private val binding get() = _binding!!


    private val recyclerAdapter = RecyclerAdapterClientesEntrenador()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientesViewModel =
            ViewModelProvider(this).get(ClientesViewModel::class.java)

        _binding = EntFragmentClientesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        /*clientesViewModel.deportistas.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()*/
        setHasOptionsMenu(true)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        clientesViewModel.deportistas.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()

        // Add the new menu items
        inflater.inflate(R.menu.main, menu)

        val search = menu?.findItem(R.id.app_bar_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar deportista"

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
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

    /*override fun onResume() {
        super.onResume()
        clientesViewModel.deportistas.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()
    }*/

    private fun setUpRecyclerView() {

        binding.tvSinClientes.isVisible = true

        if (entrenadorMain.deportistas.size > 0){

            binding.tvSinClientes.isVisible = false

            binding.rvCliente.setHasFixedSize(true)
            binding.rvCliente.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapter.RecyclerAdapter(clientesViewModel.deportistas, requireContext())
            binding.rvCliente.adapter = recyclerAdapter
        }

    }

    private fun cargarcliente (emailDep:String){
        db.collection("users").whereEqualTo(FieldPath.documentId(),emailDep)
            .addSnapshotListener{doc, exc ->
                if (exc != null){
                    Log.w(TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    for (dc in doc.documentChanges){
                        when (dc.type){
                            DocumentChange.Type.ADDED -> {
                                if (_binding != null){
                                    val deportistaDB = doc.documents[0].toObject(DeportistaDB::class.java)
                                    clientesViewModel.deportistas.add(deportistaDB!!)
                                    recyclerAdapter.RecyclerAdapter(clientesViewModel.deportistas, requireContext())
                                    recyclerAdapter.notifyDataSetChanged()
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                if (_binding != null){
                                    val deportistaDB = doc.documents[0].toObject(DeportistaDB::class.java)
                                    for (i in 0 until clientesViewModel.deportistas.size){
                                        if (clientesViewModel.deportistas[i].email == deportistaDB!!.email){
                                            if (!deportistaDB.deshabilitada){
                                                clientesViewModel.deportistas.set(i,deportistaDB)
                                                setUpRecyclerView()
                                                recyclerAdapter.notifyDataSetChanged()
                                            } else {
                                                clientesViewModel.deportistas.removeAt(i)
                                                setUpRecyclerView()
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

    private fun loadRecyclerViewAdapter(){

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""

        db.collection("users").whereEqualTo(FieldPath.documentId(), current).addSnapshotListener { doc, exc ->
            if (exc != null){
                Log.w(TAG, "Listen failed.", exc)
                return@addSnapshotListener
            }

            if (doc != null){
                if (_binding != null){
                    binding.tvSinClientes.isVisible = true
                    clientesViewModel.deportistas.clear()
                    setUpRecyclerView()
                    recyclerAdapter.notifyDataSetChanged()
                }


                for (dc in doc.documentChanges){
                    when (dc.type){
                        DocumentChange.Type.ADDED -> {
                            if (_binding != null){
                                clientesViewModel.deportistas.clear()
                                recyclerAdapter.notifyDataSetChanged()
                                val entrenadorDb = dc.document.toObject(EntrenadorDB::class.java)
                                for (emailDep:String in entrenadorDb?.deportistas!!){
                                    cargarcliente(emailDep)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            if (_binding != null){
                                clientesViewModel.deportistas.clear()
                                recyclerAdapter.notifyDataSetChanged()
                                val entrenadorDb = dc.document.toObject(EntrenadorDB::class.java)
                                for (emailDep:String in entrenadorDb?.deportistas!!){
                                    cargarcliente(emailDep)
                                }
                            }
                        }
                    }
                }



            }

        }
    }

}