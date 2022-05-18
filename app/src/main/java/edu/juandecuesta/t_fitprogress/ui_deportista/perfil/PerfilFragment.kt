package edu.juandecuesta.t_fitprogress.ui_deportista.perfil

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.DepFragmentPerfilBinding
import edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments.CondicionFragment
import edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments.HistorialFragment
import edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments.DescripcionFragment
import edu.juandecuesta.t_fitprogress.utils.ViewPager2Adapter


class PerfilFragment : Fragment() {

    private lateinit var binding: DepFragmentPerfilBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DepFragmentPerfilBinding.inflate(inflater, container, false)

        val root: View = binding.root

        //requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        val adapter = ViewPager2Adapter(childFragmentManager,lifecycle)


        //Se añaden los fragments a los tabs con sus títulos
        adapter.addFragment(DescripcionFragment(), getString(R.string.fragmentDescripcion))
        adapter.addFragment(CondicionFragment(), getString(R.string.fragmenteCondicion))
        adapter.addFragment(HistorialFragment(), getString(R.string.fragmentHistorial))

        //Asociamos el adapter al viewPager
        binding.viewPager.adapter = adapter

        //Cargamos los tabs
        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab,position -> tab.text = adapter.getPageTitle(position)
        }.attach()

        return root
    }



}