package edu.juandecuesta.t_fitprogress.ui_entrenador.pruebas_fisicas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentPruebasBinding
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding


class PruebasFragment : Fragment() {

    private lateinit var binding:EntFragmentPruebasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = EntFragmentPruebasBinding.inflate(inflater, container, false)

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