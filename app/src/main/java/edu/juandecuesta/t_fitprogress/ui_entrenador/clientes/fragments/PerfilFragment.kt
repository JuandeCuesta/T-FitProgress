package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentPerfilBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.utils.Functions


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {

    private lateinit var binding:FragmentPerfilBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPerfilBinding.inflate(inflater)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.etFCreacion.setText(deportista.fechacreacion)
        binding.etEmailDep.setText(deportista.email)
        binding.etFNacimiento.setText(deportista.fechanacimiento)
        binding.etSexo.setText(deportista.sexo)
        if (deportista.descripcionPersonal != ""){
            binding.etDescripPersonal.setText(deportista.descripcionPersonal)
        }
        if (deportista.objetivo != ""){
            binding.etObjetivos.setText(deportista.objetivo)
        }
        binding.etExperiencia.setText(deportista.experiencia)
    }

}