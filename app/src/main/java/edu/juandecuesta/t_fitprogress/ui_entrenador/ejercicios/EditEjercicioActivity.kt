package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityEditEjercicioBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.utils.GestionPermisos

class EditEjercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditEjercicioBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var gestPermisos: GestionPermisos
    private var ejercicio = Ejercicio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditEjercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.ejercicio = intent.getSerializableExtra("ejercicio") as Ejercicio

        setTitle(ejercicio.nombre)
        binding.etNombreEjerc.setText(ejercicio.nombre)
        binding.etGrupoMuscular.setText(ejercicio.grupoMuscular)
        binding.etTipoEjerc.setText(ejercicio.tipo)
        if (ejercicio.descripcion != ""){
            binding.tlInstrucciones.isVisible = true
            binding.etInstrucciones.setText(ejercicio.descripcion)
        }
        if (ejercicio.urlVideo != ""){
            binding.linLayVideo.isVisible = true
            binding.etURLVideo.setText(ejercicio.urlVideo)
        }

        Glide.with(this)
            .load(ejercicio.urlImagen)
            .error(R.drawable.icon_noimagen)
            .centerInside()
            .into(binding.imageEjerc)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.edit_ejercicio_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                onBackPressed()
                true
            }

            R.id.edit_ejerc -> {
                mostrarTodo()
                return true
            }

            R.id.delete_ejerc -> {

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarTodo(){
        binding.tLnombreEjerc.isVisible = true
        binding.linLayImagen.isVisible = true
        binding.linLayVideo.isVisible = true
        binding.etURLVideo.isFocusable = true
        binding.etGrupoMuscular.isFocusable = true
        binding.etTipoEjerc.isFocusable = true
        binding.tlInstrucciones.isVisible = true
        binding.etInstrucciones.isFocusable = true
        binding.btnGuardar.isVisible = true
    }
}