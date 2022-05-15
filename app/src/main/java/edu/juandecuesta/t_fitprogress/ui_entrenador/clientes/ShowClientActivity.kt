package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityShowClientBinding
import edu.juandecuesta.t_fitprogress.ui_entrenador.dialogAddEntrenamiento.FullDialogActivity
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes.CreateMessageActivity
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments.CondicionFragment
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments.HistorialFragment
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments.PerfilFragment
import edu.juandecuesta.t_fitprogress.utils.ViewPager2Adapter


class ShowClientActivity : AppCompatActivity() {

    private lateinit var binding:ActivityShowClientBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    companion object{
        var deportista = DeportistaDB()
        var entrenamientos:MutableList<Entrenamiento> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deportista = intent.getSerializableExtra("deportista") as DeportistaDB
        val nombreCompleto = "${deportista.nombre} ${deportista.apellido}"

        setTitle(nombreCompleto)

        val adapter = ViewPager2Adapter(supportFragmentManager,lifecycle)

        //Se añaden los fragments a los tabs con sus títulos
        adapter.addFragment(PerfilFragment(), getString(R.string.fragmentPerfil))
        adapter.addFragment(CondicionFragment(), getString(R.string.fragmenteCondicion))
        adapter.addFragment(HistorialFragment(), getString(R.string.fragmentHistorial))

        //Asociamos el adapter al viewPager
        binding.viewPager.adapter = adapter

        //Cargamos los tabs
        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab,position -> tab.text = adapter.getPageTitle(position)
        }.attach()


        binding.button.setOnClickListener {
            val myIntent = Intent (this, FullDialogActivity::class.java)
            startActivity(myIntent)
        }

        cargarEntrenamientos()
    }

    private fun cargarEntrenamientos(){
        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").document(current)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                    entrenamientos.clear()

                    for (idEntren:String in entrenadorDb?.entrenamientos!!){

                        db.collection("entrenamientos").whereEqualTo(FieldPath.documentId(),idEntren)
                            .addSnapshotListener{doc, exc ->

                                if (doc != null){
                                    for (dc in doc.documentChanges){
                                        when (dc.type){
                                            DocumentChange.Type.ADDED -> {
                                                val entren = doc.documents[0].toObject(
                                                    Entrenamiento::class.java)
                                                entrenamientos.add(entren!!)
                                            }
                                            DocumentChange.Type.MODIFIED -> {
                                                val entren = doc.documents[0].toObject(
                                                    Entrenamiento::class.java)
                                                for (i in 0 until entrenamientos.size){
                                                    if (entrenamientos[i].id == entren!!.id){
                                                        entrenamientos.set(i,entren)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }

                            }
                    }

                }
            }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.menu_show_deportista, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()

                true
            }
            R.id.sendMessage -> {
                val messageIntent = Intent (this, CreateMessageActivity::class.java).apply {
                    putExtra("deportista", deportista)
                }
                startActivity(messageIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}