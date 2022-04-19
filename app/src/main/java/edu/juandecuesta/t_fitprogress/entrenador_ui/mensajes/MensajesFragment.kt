package edu.juandecuesta.t_fitprogress.entrenador_ui.mensajes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.juandecuesta.t_fitprogress.databinding.FragmentMensajesBinding
import edu.juandecuesta.t_fitprogress.entrenador_ui.entrenamientos.EntrenamientosViewModel

class MensajesFragment:Fragment() {
    private lateinit var mensajesViewModel: MensajesViewModel
    private var _binding: FragmentMensajesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mensajesViewModel =
            ViewModelProvider(this).get(MensajesViewModel::class.java)

        _binding = FragmentMensajesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        binding.textMensaje.text = mensajesViewModel.texto

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}