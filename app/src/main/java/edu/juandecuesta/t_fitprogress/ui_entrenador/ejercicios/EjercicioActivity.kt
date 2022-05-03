package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

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
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Task
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.juandecuesta.t_fitprogress.databinding.ActivityEjercicioBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity.Companion.entrenador
import edu.juandecuesta.t_fitprogress.utils.Functions
import edu.juandecuesta.t_fitprogress.utils.GestionPermisos
import java.io.File
import java.util.*

class EjercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEjercicioBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var gestPermisos: GestionPermisos

    private val MY_PERMISSIONS_REQUEST_CAMERA = 234

    val key = "AIzaSyA4z9gUsYmXJ36ovpfzghbyboexc3ksPCo"
    var idVideo = ""
    var uri_img: Uri ?=null
    var imgCorrecta = false
    // Variable para crear el nombre del archivo.
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEjercicioBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.rbVideo.setOnClickListener {
            if (binding.rbVideo.isChecked){
                binding.linLayVideo.isVisible = true
                binding.linLayImagen.isVisible = false

            }
        }

        binding.rbImagen.setOnClickListener {
            if (binding.rbImagen.isChecked){
                binding.linLayVideo.isVisible = false
                binding.linLayImagen.isVisible = true
            }
        }

        binding.rbAmbos.setOnClickListener {
            if (binding.rbAmbos.isChecked){
                binding.linLayVideo.isVisible = true
                binding.linLayImagen.isVisible = true
            }
        }

        binding.rbNada.setOnClickListener {
            if (binding.rbNada.isChecked){
                binding.linLayVideo.isVisible = false
                binding.linLayImagen.isVisible = false
            }
        }

        binding.btnVerVideo.setOnClickListener {

            if (comprobarURL()){
                openYoutubeStandAlonePlayer(idVideo,false,true)
            }
        }

        binding.btnGaleria.setOnClickListener {
            requestPermission()
        }

        binding.btnHacerFoto.setOnClickListener {
            requestPermissionCamera()
        }

        binding.btnGuardar.setOnClickListener {

            if (binding.rbNada.isChecked){
                if (comprobarCamposObligatorios()){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    val ejercicio = crearEjercicio()
                    guardarEjercicio(ejercicio)
                }
            } else if (binding.rbVideo.isChecked){
                if (comprobarCamposObligatorios() && comprobarURL()){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    val ejercicio = crearEjercicio()
                    guardarEjercicio(ejercicio)
                }
            } else if (binding.rbImagen.isChecked) {
                if (comprobarCamposObligatorios() && imgCorrecta){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    guardarImagen()
                }
            }else{
                if (comprobarCamposObligatorios() && imgCorrecta && comprobarURL()){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    guardarImagen()
                }
            }
        }

    }

    private fun guardarImagen(){
        val ejercicio = crearEjercicio()
        val storageRef = FirebaseStorage.getInstance().reference
        val folderRef = storageRef.child("imagesEjercicios")
        val code = Functions().buscarCodeImg(uri_img.toString()) +  "_" + Date().toString()
        val fotoRef = folderRef.child(code)

        fotoRef.putFile(uri_img!!).addOnSuccessListener {
            val uriTask:Task<Uri> = it.storage.downloadUrl
            while(!uriTask.isSuccessful){}
            val downloadUri:Uri = uriTask.result
            ejercicio.urlImagen = downloadUri.toString()

            guardarEjercicio(ejercicio)
        }. addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al subir la imagen a la nube")
        }
    }

    private fun guardarEjercicio(ejercicio: Ejercicio){

        db.collection("ejercicios").add(ejercicio).addOnSuccessListener {

            it.get().addOnSuccessListener {
                doc ->
                val id = doc.id
                db.collection("ejercicios").document(id).update("id",id).addOnSuccessListener {
                    db.collection("users").document(entrenador.email).get().addOnSuccessListener {document->
                        var ejercicios:MutableList<String> = arrayListOf()

                        if (document.get("ejercicios") != null){
                            ejercicios = document.get("ejercicios") as MutableList<String>
                        }

                        ejercicios.add(id)

                        db.collection("users").document(entrenador.email)
                            .update("ejercicios", ejercicios).addOnSuccessListener{
                                Toast.makeText(this, "Ejercicio creado con éxito", Toast.LENGTH_LONG).show()
                                onBackPressed()
                            }.addOnFailureListener {
                                binding.lyProgress.isVisible = false
                                binding.btnGuardar.isVisible = true
                                Functions().showSnackSimple(binding.root,"Ha habido un error al crear el ejercicio")
                            }
                    }

                }.addOnFailureListener {
                    binding.lyProgress.isVisible = false
                    binding.btnGuardar.isVisible = true
                    Functions().showSnackSimple(binding.root,"Ha habido un error al crear el ejercicio")
                }
            }

        }.addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al crear el ejercicio")
        }

    }

    private fun crearEjercicio():Ejercicio{

        val ejercicio = Ejercicio()
        ejercicio.nombre = binding.etNombreEjerc.text.toString()
        ejercicio.descripcion = if (TextUtils.isEmpty(binding.etInstrucciones.text.toString())) "" else binding.etInstrucciones.text.toString()
        ejercicio.urlVideo = if (TextUtils.isEmpty(binding.etURLVideo.text.toString())) "" else binding.etURLVideo.text.toString()
        ejercicio.grupoMuscular = binding.etGrupoMuscular.text.toString()
        ejercicio.tipo = binding.etTipoEjerc.text.toString()

        return ejercicio
    }

    private fun comprobarCamposObligatorios(): Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etNombreEjerc.text.toString())){
            binding.tLnombreEjerc.error = "Información requerida"
            valido = false
        } else binding.tLnombreEjerc.error = null

        if (TextUtils.isEmpty(binding.etGrupoMuscular.text.toString())){
            binding.tlGrupoMuscular.error = "Información requerida"
            valido = false
        } else binding.tlGrupoMuscular.error = null

        if (TextUtils.isEmpty(binding.etTipoEjerc.text.toString())){
            binding.tlTipoEjerc.error = "Información requerida"
            valido = false
        } else binding.tlTipoEjerc.error = null

        return valido
    }

    private fun requestPermissionCamera() {

        // Verificaremos el nivel de API para solicitar los permisos
        // en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED -> {
                        takePhoto()
                }

                else -> requestPermissionCamera.launch(Manifest.permission.CAMERA)
            }
        }else {
            takePhoto()
        }

    }

    private val requestPermissionCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){
            takePhoto()
        }else{
            Toast.makeText(
                this,
                "Permiso denegado",
                Toast.LENGTH_SHORT).show()
        }
    }
    private fun takePhoto(){
        // Se crea el fichero done se guardará la imagen.
        photoFile = File(getExternalFilesDir(null),"test.jpg")

        uri_img =
            FileProvider.getUriForFile(this,"edu.juandecuesta.t_fitprogress.provider",photoFile!!)

        val intentCamera = Intent(Intent(MediaStore.ACTION_IMAGE_CAPTURE)).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri_img)
        }

        if (intentCamera.resolveActivity(packageManager) != null) {
            resultTakePicture.launch(intentCamera)
        }
    }

    private fun requestPermission() {
        // Verificaremos el nivel de API para solicitar los permisos
        // en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            pickPhotoFromGallery()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){
            pickPhotoFromGallery()
        }else{
            Toast.makeText(
                this,
                "Permission denied",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }

    var resultTakePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val ruta = "${this.getExternalFilesDir(null)}/test.jpg"
            val bitmap = BitmapFactory.decodeFile(ruta)
            val data = result.data?.data
            binding.imageEjerc.isVisible = true
            binding.imageEjerc.setImageBitmap(bitmap)
            imgCorrecta = true

        } else { // Mensaje advirtiendo que no se ha podido guardar la imagen
            Toast.makeText(
                applicationContext,
                "No se ha podido capturar la imagen",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data?.data
            binding.imageEjerc.isVisible = true
            uri_img = data
            binding.imageEjerc.setImageURI(uri_img)
            imgCorrecta = true
        }
    }

    fun openYoutubeStandAlonePlayer(VideoID: String, autoplay: Boolean = false, lightMode: Boolean = false) {
        val intent = YouTubeStandalonePlayer.createVideoIntent(
            this,key, VideoID,0, autoplay,lightMode)
        startActivity(intent)
    }

    fun comprobarURL():Boolean{
        var valido = false
        if (binding.etURLVideo.text.toString().contains("youtube.com/watch")){
            idVideo = Functions().buscarIdURL(binding.etURLVideo.text.toString())
            valido = true

        } else if (TextUtils.isEmpty(binding.etURLVideo.text.toString())) {
            binding.tlURLvideo.error = "URL sin especificar"
        }else binding.tlURLvideo.error = "La URL no es correcta"
        return valido
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}