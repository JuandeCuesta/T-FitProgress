package edu.juandecuesta.t_fitprogress.entrenador_ui.entrenamientos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEntrenamientosBinding

class EntrenamientosFragment:Fragment() {
    private lateinit var entrenamientosViewModel: EntrenamientosViewModel
    private var _binding: EntFragmentEntrenamientosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        entrenamientosViewModel =
            ViewModelProvider(this).get(EntrenamientosViewModel::class.java)

        _binding = EntFragmentEntrenamientosBinding.inflate(inflater, container, false)

        val root: View = binding.root

        binding.textEntrenamiento.text = entrenamientosViewModel.texto

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}