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
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
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
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.io.File
import java.util.*

class EditEjercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditEjercicioBinding
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

        binding = ActivityEditEjercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.ejercicio = intent.getSerializableExtra("ejercicio") as Ejercicio

        setTitle(ejercicio.nombre)

        val type = arrayOf("Resistencia", "Fuerza", "Flexibilidad", "Velocidad")

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, type)
        binding.etTipoEjerc.setAdapter(adapter)

        cargarDatos()

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

        binding.btnQuitarFoto.setOnClickListener {
            Glide.with(this)
                .load(R.drawable.icon_noimagen)
                .centerInside()
                .into(binding.imageEjerc)

            quitarImagen = true
            ejercicio.urlVideo = ""
        }

        binding.btnGuardar.setOnClickListener {
            if (comprobarCamposObligatorios()){
                if (TextUtils.isEmpty(binding.etURLVideo.text.toString())){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    actualizarDatos()
                    if (imagenActualizada && !quitarImagen){
                        guardarImagen()
                    }else{
                        guardarEjercicio(ejercicio)
                    }
                }else if (comprobarURL()){
                    binding.lyProgress.isVisible = true
                    binding.btnGuardar.isVisible = false
                    actualizarDatos()
                    if (imagenActualizada && !quitarImagen){
                        guardarImagen()
                    }else{
                        guardarEjercicio(ejercicio)
                    }
                }
            }
        }

    }

    private fun actualizarDatos(){
        ejercicio.nombre = binding.etNombreEjerc.text.toString()
        ejercicio.descripcion = if (TextUtils.isEmpty(binding.etInstrucciones.text.toString())) "" else binding.etInstrucciones.text.toString()
        ejercicio.urlVideo = if (TextUtils.isEmpty(binding.etURLVideo.text.toString())) "" else binding.etURLVideo.text.toString()
        ejercicio.grupoMuscular = binding.etGrupoMuscular.text.toString()
        ejercicio.tipo = binding.etTipoEjerc.text.toString()
    }
    private fun guardarImagen(){
        val storageRef = FirebaseStorage.getInstance().reference
        val folderRef = storageRef.child("imagesEjercicios")
        val code = Functions().buscarCodeImg(uri_img.toString()) +  "_" + Date().toString()
        val fotoRef = folderRef.child(code)

        fotoRef.putFile(uri_img!!).addOnSuccessListener {
            val uriTask: Task<Uri> = it.storage.downloadUrl
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

        db.collection("ejercicios").document(ejercicio.id).set(ejercicio).addOnSuccessListener {
            Toast.makeText(this, "Ejercicio actualizado con éxito", Toast.LENGTH_LONG).show()
            setTitle(ejercicio.nombre)
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            noMostrar()
        }.addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.btnGuardar.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al actualizar el ejercicio")
        }

    }

    fun cargarDatos(){
        binding.etNombreEjerc.setText(ejercicio.nombre)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.edit_ejercicio_menu, menu)
        return true
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

    fun delete(){
        db.collection("users").document(MainActivity.entrenadorMain.email).get().addOnSuccessListener{ doc->

            var ejercicios:MutableList<String> = arrayListOf()

            if (doc.get("ejercicios") != null){
                ejercicios = doc.get("ejercicios") as MutableList<String>
                ejercicios.remove(ejercicio.id)
            }

            db.collection("users").document(MainActivity.entrenadorMain.email)
                .update("ejercicios", ejercicios).addOnSuccessListener{
                    Toast.makeText(this, "El ejercicio ha sido eliminado con éxito", Toast.LENGTH_LONG).show()

                    onBackPressed()
                }.addOnFailureListener {
                    Functions().showSnackSimple(binding.root,"Ha habido un error al eliminar el ejercicio")
                }

        }
    }

    private fun volver_Pagina(){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setTitle("Volver a la página anterior")
            setMessage("Perderás todos los datos no guardados, ¿estás seguro?")
            setPositiveButton(
                android.R.string.ok){_,_->super.onBackPressed()}
            setNegativeButton(
                android.R.string.cancel){_,_->}
        }
        builder.show()
    }

    override fun onBackPressed() {
        if (modoEdit){
            volver_Pagina()
        }else{
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                if (modoEdit){
                    volver_Pagina()
                } else {
                    onBackPressed()
                }
                true
            }

            R.id.edit_ejerc -> {
                mostrarTodo()

                return true
            }

            R.id.delete_ejerc -> {
                val builder = AlertDialog.Builder(this)

                builder.apply {
                    setTitle("Eliminar ejercicio")
                    setMessage("¿Estás seguro?")
                    setPositiveButton(
                        android.R.string.ok){_,_->delete()}
                    setNegativeButton(
                        android.R.string.cancel){_,_->
                        Toast.makeText(
                            context, "El ejercicio no ha sido eliminado",
                            Toast.LENGTH_LONG).show()}
                }
                builder.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun noMostrar(){
        modoEdit = false
        binding.tLnombreEjerc.isVisible = false
        binding.linLayImagen.isVisible = false

        if (ejercicio.urlVideo == ""){
            binding.linLayVideo.isVisible = false
        }
        binding.etURLVideo.isFocusableInTouchMode = false
        binding.etGrupoMuscular.isFocusableInTouchMode = false
        binding.etTipoEjerc.isClickable = false

        binding.etInstrucciones.clearFocus()
        binding.etInstrucciones.isFocusableInTouchMode = false
        binding.btnGuardar.isVisible = false
    }

    private fun mostrarTodo(){

        modoEdit = true
        binding.tLnombreEjerc.isVisible = true
        binding.linLayImagen.isVisible = true
        binding.linLayVideo.isVisible = true
        binding.etURLVideo.isFocusableInTouchMode = true
        binding.etGrupoMuscular.isFocusableInTouchMode = true
        binding.etTipoEjerc.isClickable = true
        binding.tlInstrucciones.isVisible = true
        binding.etInstrucciones.isFocusableInTouchMode = true
        binding.btnGuardar.isVisible = true
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

    fun openYoutubeStandAlonePlayer(VideoID: String, autoplay: Boolean = false, lightMode: Boolean = false) {
        val intent = YouTubeStandalonePlayer.createVideoIntent(
            this,key, VideoID,0, autoplay,lightMode)
        startActivity(intent)
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

                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
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
            Glide.with(this)
                .load(uri_img)
                .error(R.drawable.icon_noimagen)
                .centerInside()
                .into(binding.imageEjerc)
            ejercicio.urlImagen = ""
            imagenActualizada = true
            quitarImagen = false

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
            Glide.with(this)
                .load(uri_img)
                .error(R.drawable.icon_noimagen)
                .centerInside()
                .into(binding.imageEjerc)
            ejercicio.urlImagen = ""
            imagenActualizada = true
            quitarImagen = false
        }
    }
}