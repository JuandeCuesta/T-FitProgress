package edu.juandecuesta.t_fitprogress

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityMainBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity
import edu.juandecuesta.t_fitprogress.utils.Functions

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object{
        lateinit var db:FirebaseFirestore
        var entrenadorMain: EntrenadorDB = EntrenadorDB()
        var deportistaMain = DeportistaDB()
        var esentrenador:Boolean = false
        lateinit var searchView: SearchView
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        esentrenador = intent.getBooleanExtra("esentrenador", false)
        if (esentrenador){
            entrenadorMain = intent.getSerializableExtra("entrenador") as EntrenadorDB
        } else{
            deportistaMain = intent.getSerializableExtra("deportista") as DeportistaDB

            db.collection("users").document(deportistaMain.entrenador).get().addOnSuccessListener {
                    doc -> entrenadorMain = doc.toObject(EntrenadorDB::class.java)!!
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (esentrenador){
            setSupportActionBar(binding.appBarMain.toolbar)
            val drawerLayout: DrawerLayout = binding.drawerLayout
            val navView: NavigationView = binding.navView
            val headView: View = navView.getHeaderView(0)
            val emailText = headView.findViewById<TextView>(R.id.tvEmail)
            val nombre = headView.findViewById<TextView>(R.id.tvNombreUser)
            emailText.text = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val nombreCompleto = "${entrenadorMain.nombre} ${entrenadorMain.apellido}"
            nombre.text = nombreCompleto
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_clientes,
                    R.id.nav_ejercicios,
                    R.id.nav_entrenamientos,
                    R.id.nav_evaluacion,
                    R.id.nav_mensajes
                ), drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else{
            setSupportActionBar(binding.appBarMain.toolbar)
            val drawerLayout: DrawerLayout = binding.drawerLayout
            val navView: NavigationView = binding.navView
            val headView: View = navView.getHeaderView(0)
            val emailText = headView.findViewById<TextView>(R.id.tvEmail)
            val nombre = headView.findViewById<TextView>(R.id.tvNombreUser)
            val imageNormal = headView.findViewById<ImageView>(R.id.imageView)
            val imagePerfil = headView.findViewById<ImageView>(R.id.imageViewDeportista)
            val cardview = headView.findViewById<CardView>(R.id.cardviewimagendep)


            db.collection("users").whereEqualTo(FieldPath.documentId(), deportistaMain.email)
                .addSnapshotListener { doc, error ->
                    if (error != null){
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (doc != null){
                        for (dc in doc.documentChanges){
                            when (dc.type){
                                DocumentChange.Type.MODIFIED ->
                                {
                                    deportistaMain = dc.document.toObject(DeportistaDB::class.java)

                                    if (!esentrenador){
                                        if (deportistaMain.deshabilitada){
                                            Functions().showSnackSimple(binding.root, "Tu cuenta ha sido deshabilitada")
                                            cerrarSesion()
                                        }

                                        Glide.with(this)
                                            .load(deportistaMain.urlImagen)
                                            .error(R.drawable.ic_person_white)
                                            .into(imagePerfil)

                                        val nombreCompleto = "${deportistaMain.nombre} ${deportistaMain.apellido}"
                                        nombre.text = nombreCompleto
                                    }
                                }
                            }
                        }
                    }
                }

            Glide.with(this)
                .load(deportistaMain.urlImagen)
                .error(R.drawable.ic_person_white)
                .into(imagePerfil)

            imageNormal.isVisible = false
            cardview.isVisible = true
            emailText.text = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val nombreCompleto = "${deportistaMain.nombre} ${deportistaMain.apellido}"
            nombre.text = nombreCompleto
            val navController = findNavController(R.id.nav_host_fragment_content_main)

            navView.menu.clear()
            navView.inflateMenu(R.menu.activity_main_deportista_drawer)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_calendario_deportista,
                    R.id.nav_evaluacion_deportista,
                    R.id.nav_mensajes_deportista,
                    R.id.nav_perfil_deportista
                ), drawerLayout
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

        binding.logout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion (){
        FirebaseAuth.getInstance().signOut()

        val myIntent = Intent (this, LoginActivity::class.java)
        startActivity(myIntent)
        finish()
        db.terminate()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

        if(binding.drawerLayout.isOpen){
            binding.drawerLayout.close()
        }else if (!searchView.isIconified()){
            searchView.setIconified(true)
        } else {
            super.onBackPressed()
        }
    }

}