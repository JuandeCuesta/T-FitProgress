package edu.juandecuesta.t_fitprogress.ui_deportista.evaluacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding


class EvaluacionFragment : Fragment() {

    private lateinit var binding:FragmentEvaluacionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEvaluacionBinding.inflate(inflater, container, false)

        val type = arrayOf("Flexiones normales", "Flexiones modificadas")
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_menu_popup_item, type)
        binding.etTIpoFlexion.setAdapter(adapter)

        binding.TestIMCMore.setOnClickListener {
            binding.TestIMCMore.isVisible = false
            binding.lyIMCMore.isVisible = true
        }

        binding.TestIMCLess.setOnClickListener {
            binding.TestIMCMore.isVisible = true
            binding.lyIMCMore.isVisible = false
        }

        binding.TestFuerzaMore.setOnClickListener {
            binding.TestFuerzaMore.isVisible = false
            binding.lyFuerzaMore.isVisible = true
        }

        binding.TestFuerzaLess.setOnClickListener {
            binding.TestFuerzaMore.isVisible = true
            binding.lyFuerzaMore.isVisible = false
        }

        binding.TestCooperMore.setOnClickListener {
            binding.TestCooperMore.isVisible = false
            binding.lyCooperMore.isVisible = true
        }

        binding.TestCooperLess.setOnClickListener {
            binding.TestCooperMore.isVisible = true
            binding.lyCooperMore.isVisible = false
        }

        return binding.root
    }

}