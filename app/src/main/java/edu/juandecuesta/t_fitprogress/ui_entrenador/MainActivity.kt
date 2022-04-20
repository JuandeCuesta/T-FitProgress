package edu.juandecuesta.t_fitprogress.ui_entrenador

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import edu.juandecuesta.t_fitprogress.LoginActivity
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityMainBinding
import edu.juandecuesta.t_fitprogress.model.Entrenador

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object{
        lateinit var entrenador: Entrenador
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        entrenador = Entrenador()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_clientes,
                R.id.nav_ejercicios,
                R.id.nav_entrenamientos,
                R.id.nav_mensajes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.logout.setOnClickListener {
            val myIntent = Intent (this, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isOpen){
            binding.drawerLayout.close()
        }else super.onBackPressed()
    }

}