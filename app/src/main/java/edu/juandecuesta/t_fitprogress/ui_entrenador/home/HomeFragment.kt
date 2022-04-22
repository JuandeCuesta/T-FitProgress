package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentHomeBinding
import android.view.MenuInflater




class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: EntFragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = EntFragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textHome.text = homeViewModel.fecha
        setUpRecyclerView()

        setHasOptionsMenu(true);

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRV.isVisible = true

        //Si el listado tiene algún dato se quitará el textview y se cargará el adapter en el recyclerview
        if (homeViewModel.deportistas != null && homeViewModel.deportistas?.size!! > 0) {

            binding.tvInfoRV.isVisible = false

            binding.rvhome.setHasFixedSize(true)
            binding.rvhome.layoutManager = LinearLayoutManager(requireContext())

            //recyclerAdapter.RecyclerAdapter(homeViewModel.deportistas!!, requireContext())
            binding.rvhome.adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()

        // Add the new menu items
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.nav1 -> {
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}