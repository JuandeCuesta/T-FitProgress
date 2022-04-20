package edu.juandecuesta.t_fitprogress.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.juandecuesta.t_fitprogress.databinding.FragmentRegisterEntrenadorBinding

class RegisterEntrenadorFragment : Fragment() {
    private lateinit var binding: FragmentRegisterEntrenadorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterEntrenadorBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }
}