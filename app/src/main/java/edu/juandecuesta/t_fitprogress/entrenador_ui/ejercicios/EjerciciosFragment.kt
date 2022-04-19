package edu.juandecuesta.t_fitprogress.entrenador_ui.ejercicios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentEjerciciosBinding

class EjerciciosFragment : Fragment() {

    private lateinit var ejerciciosViewModel: EjerciciosViewModel
    private var _binding: EntFragmentEjerciciosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ejerciciosViewModel =
            ViewModelProvider(this).get(EjerciciosViewModel::class.java)

        _binding = EntFragmentEjerciciosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textEjercicios
        ejerciciosViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}