package edu.juandecuesta.t_fitprogress.ui_entrenador.pruebas_fisicas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }

}