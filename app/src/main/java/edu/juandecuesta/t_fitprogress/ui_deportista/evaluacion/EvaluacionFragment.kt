package edu.juandecuesta.t_fitprogress.ui_deportista.evaluacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentEvaluacionBinding


class EvaluacionFragment : Fragment() {

    private lateinit var binding:FragmentEvaluacionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEvaluacionBinding.inflate(inflater, container, false)

        return binding.root
    }

}