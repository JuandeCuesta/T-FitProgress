package edu.juandecuesta.t_fitprogress.ui_deportista.entrenamientos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ActivityEditEjercicioBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.databinding.ActivityShowEjercicioBinding
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.io.File
import java.util.*

class ShowEjercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowEjercicioBinding
    private val db = FirebaseFirestore.getInstance()
    private var ejercicio = Ejercicio()

    val key = "AIzaSyA4z9gUsYmXJ36ovpfzghbyboexc3ksPCo"
    var idVideo = ""

    //Ver si ha cambiado la imagen o no
    var imagenActualizada = false
    var modoEdit = false
    var quitarImagen = false
    var uri_img: Uri?=null
    // Variable para crear el nombre del archivo.
    private var photoFile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowEjercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.ejercicio = intent.getSerializableExtra("ejercicio") as Ejercicio

        setTitle(ejercicio.nombre)

        cargarDatos()

        binding.btnVerVideo.setOnClickListener {
            openYoutubeStandAlonePlayer(idVideo,false,true)
        }
    }

    fun cargarDatos(){

        binding.etGrupoMuscular.setText(ejercicio.grupoMuscular)
        binding.etTipoEjerc.setText(ejercicio.tipo)

        if (ejercicio.descripcion != ""){
            binding.tlInstrucciones.isVisible = true
            binding.etInstrucciones.setText(ejercicio.descripcion)
        }

        Glide.with(this)
            .load(ejercicio.urlImagen)
            .error(R.drawable.icon_noimagen)
            .centerInside()
            .into(binding.imageEjerc)


        if (ejercicio.urlImagen == ""){
            Glide.with(this)
                .load(uri_img)
                .error(R.drawable.icon_noimagen)
                .centerInside()
                .into(binding.imageEjerc)
        }

        if (ejercicio.urlVideo != ""){
            binding.linLayVideo.isVisible = true
            binding.etURLVideo.setText(ejercicio.urlVideo)


            Glide.with(this)
                .load(ejercicio.urlImagen)
                .error(R.drawable.icon_video)
                .centerInside()
                .into(binding.imageEjerc)

        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openYoutubeStandAlonePlayer(VideoID: String, autoplay: Boolean = false, lightMode: Boolean = false) {
        val intent = YouTubeStandalonePlayer.createVideoIntent(
            this,key, VideoID,0, autoplay,lightMode)
        startActivity(intent)
    }

}