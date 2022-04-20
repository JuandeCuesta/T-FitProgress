package edu.juandecuesta.t_fitprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import edu.juandecuesta.t_fitprogress.databinding.ActivityRegisterBinding
import edu.juandecuesta.t_fitprogress.register.RegisterDeportistaFragment
import edu.juandecuesta.t_fitprogress.register.RegisterEntrenadorFragment
import edu.juandecuesta.t_fitprogress.register.ViewPager2Adapter

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Se crea el adpater
        val adapter = ViewPager2Adapter(supportFragmentManager,lifecycle)

        //Se añaden los fragments a los tabs con sus títulos
        adapter.addFragment(RegisterEntrenadorFragment(), getString(R.string.fragmentEntrenador))
        adapter.addFragment(RegisterDeportistaFragment(), getString(R.string.fragmentDeportista))

        //Asociamos el adapter al viewPager
        binding.viewPager.adapter = adapter

        //Cargamos los tabs
        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab,position -> tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}