package edu.juandecuesta.t_fitprogress.entrenador_ui.clientes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding

class ClientesFragment : Fragment() {

    private lateinit var clientesViewModel: ClientesViewModel
    private var _binding: EntFragmentClientesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientesViewModel =
            ViewModelProvider(this).get(ClientesViewModel::class.java)

        _binding = EntFragmentClientesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        binding.textClientes.text = clientesViewModel.texto

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}