package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.home.RecyclerAdapterHomeEntrenador
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity.Companion.entrenador


class ClientesFragment : Fragment() {

    private lateinit var clientesViewModel: ClientesViewModel
    private var _binding: EntFragmentClientesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapterClientesEntrenador()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientesViewModel =
            ViewModelProvider(this).get(ClientesViewModel::class.java)

        _binding = EntFragmentClientesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        clientesViewModel.deportistas.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {

        if (entrenador.deportistas.size > 0){

            binding.rvCliente.setHasFixedSize(true)
            binding.rvCliente.layoutManager = LinearLayoutManager(requireContext())

            binding.rvCliente.setHasFixedSize(true)
            binding.rvCliente.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapter.RecyclerAdapter(clientesViewModel.deportistas, requireContext())
            binding.rvCliente.adapter = recyclerAdapter
        }


    }

    private fun loadRecyclerViewAdapter(){

        for (emailDep:String in entrenador.deportistas){

            db.collection("users").document(emailDep).get().addOnSuccessListener {
                if (it != null){
                    val deportistaDB = it.toObject(DeportistaDB::class.java)
                    clientesViewModel.deportistas.add(deportistaDB!!)
                    recyclerAdapter.RecyclerAdapter(clientesViewModel.deportistas, requireContext())
                    recyclerAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                binding.textClientes.text = "Error"
            }

        }

    }

}