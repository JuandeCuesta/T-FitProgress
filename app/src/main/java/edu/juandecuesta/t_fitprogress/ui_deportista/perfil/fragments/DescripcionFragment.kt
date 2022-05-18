package edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentPerfilDeportistaBinding
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.util.*


class DescripcionFragment : Fragment() {

    private lateinit var binding:FragmentPerfilDeportistaBinding
    var uri_img: Uri?=null
    var imgCorrecta = false
    var imagenActualizada = false
    var modoEdit = false
    val textoEjemplo = "Soy personal administrativo, me considero activo, dispongo de 30min al dia para realizar ejercicio ..."

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPerfilDeportistaBinding.inflate(inflater)
        val root: View = binding.root
        cargar()


        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            if (modoEdit){
                val builder = AlertDialog.Builder(requireContext())

                builder.apply {
                    setTitle("Editar perfil")
                    setMessage("Si vuelves atrás perderás los datos no guardados, ¿estás seguro?")
                    setPositiveButton(
                        android.R.string.ok){_,_->
                        if (!findNavController().navigateUp()) {
                            if (isEnabled) {
                                isEnabled = false
                            }
                        }

                    }
                    setNegativeButton(
                        android.R.string.cancel){_,_->}
                }
                builder.show()

            } else {
                if (!findNavController().navigateUp()) {
                    if (isEnabled) {
                        isEnabled = false
                    }
                }
            }
        }

        binding.etDescripPersonal.setOnClickListener {
            if ((binding.etDescripPersonal.text.toString() == requireContext().resources.getString(R.string.descripcion_basica_extendida)) && deportistaMain.descripcionPersonal == ""){
                binding.etDescripPersonal.setText("")
            }
        }
        binding.etDescripPersonal.setOnFocusChangeListener { view, b ->
            if (view.isFocused){
                if ((binding.etDescripPersonal.text.toString() == requireContext().resources.getString(R.string.descripcion_basica_extendida)) && deportistaMain.descripcionPersonal == ""){
                    binding.etDescripPersonal.setText("")
                }
            }
        }

        binding.editarFoto.setOnClickListener {
            requestPermission()
        }

        binding.btnEditarPerfil.setOnClickListener {

            if (!modoEdit){
                binding.lyMostrar.isVisible = false
                binding.lyEditar.isVisible = true
                modoEdit = true
                binding.btnEditarPerfil.text = "Guardar cambios"

            }else {

                if (comprobarNombre()){
                    binding.btnEditarPerfil.isVisible = false
                    binding.lyProgress.isVisible = true
                    binding.textView.isVisible = true
                    deportistaMain.nombre = binding.etNombre.text.toString()
                    deportistaMain.apellido = binding.apellido1.text.toString()
                    deportistaMain.objetivo = binding.etObjetivos.text.toString()
                    deportistaMain.descripcionPersonal = if (binding.etDescripPersonal.text.toString() != textoEjemplo) binding.etDescripPersonal.text.toString() else ""
                    if (imagenActualizada){
                        guardarImagen()
                    } else {
                        guardarCambios()
                    }
                }

            }

        }

        return root
    }

    private fun comprobarNombre(): Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etNombre.text.toString())){
            binding.tLnombre.error = "Información requerida"
            valido = false
        } else binding.tLnombre.error = null

        return valido
    }

    private fun guardarImagen(){
        val storageRef = FirebaseStorage.getInstance().reference
        val folderRef = storageRef.child("imagesPerfil")
        val code = Functions().buscarCodeImg(uri_img.toString()) +  "_" + Date().toString()
        val fotoRef = folderRef.child(code)

        fotoRef.putFile(uri_img!!).addOnSuccessListener {
            val uriTask: Task<Uri> = it.storage.downloadUrl
            while(!uriTask.isSuccessful){}
            val downloadUri:Uri = uriTask.result
            deportistaMain.urlImagen = downloadUri.toString()
            guardarCambios()
        }. addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.textView.isVisible = false
            binding.btnEditarPerfil.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al subir la imagen a la nube")
        }
    }

    private fun guardarCambios(){

        db.collection("users").document(deportistaMain.email).set(deportistaMain).addOnSuccessListener {
            Toast.makeText(requireContext(), "Perfil actualizado con éxito", Toast.LENGTH_LONG).show()
            binding.lyProgress.isVisible = false
            binding.textView.isVisible = false
            binding.btnEditarPerfil.isVisible = true
            actualizarDatos()
        }.addOnFailureListener {
            binding.lyProgress.isVisible = false
            binding.textView.isVisible = false
            binding.btnEditarPerfil.isVisible = true
            Functions().showSnackSimple(binding.root,"Ha habido un error al actualizar el perfil")
        }

    }

    private fun actualizarDatos() {

        val nombreCompleto = "${deportistaMain.nombre} ${deportistaMain.apellido}"
        binding.nombreShow.text = nombreCompleto
        binding.etExperiencia.setText(deportistaMain.experiencia)
        binding.etObjetivosShow.setText(deportistaMain.objetivo)
        binding.etObjetivos.setText(deportistaMain.objetivo)
        binding.etDescripPersonalShow.setText(deportistaMain.descripcionPersonal)
        if (deportistaMain.descripcionPersonal != ""){
            binding.etDescripPersonal.setText(deportistaMain.descripcionPersonal)
        } else binding.etDescripPersonal.setText(R.string.descripcion_basica_extendida)

        Glide.with(this)
            .load(deportistaMain.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imagenDeportista)

        Glide.with(this)
            .load(deportistaMain.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imageShow)

        binding.lyMostrar.isVisible = true
        binding.lyEditar.isVisible = false
        modoEdit = false
        imagenActualizada = false
        binding.btnEditarPerfil.text = "Editar perfil"

    }

    fun cargar () {

        val array = requireContext().resources.getStringArray(R.array.material_calendar_months_array)

        val nombreCompleto = "${deportistaMain.nombre} ${deportistaMain.apellido}"
        val dia = deportistaMain.fechacreacion.split("/")[0]
        val mes = deportistaMain.fechacreacion.split("/")[1].toInt()
        val anyo = deportistaMain.fechacreacion.split("/")[2].toInt()
        val fechaRegistro = "$dia de ${array[mes-1]} de $anyo"

        val diaB = deportistaMain.fechanacimiento.split("/")[0]
        val mesB = deportistaMain.fechanacimiento.split("/")[1].toInt()
        val anyoB = deportistaMain.fechanacimiento.split("/")[2].toInt()
        val fechaNacimiento = "$diaB de ${array[mesB-1]} de $anyoB"

        binding.nombreShow.text = nombreCompleto
        binding.etFCreacion.setText(fechaRegistro)
        binding.etEmailDep.setText(deportistaMain.email)
        binding.etFNacimiento.setText(fechaNacimiento)
        binding.etSexo.setText(deportistaMain.sexo)
        binding.etExperiencia.setText(deportistaMain.experiencia)
        binding.etObjetivosShow.setText(deportistaMain.objetivo)
        binding.etObjetivos.setText(deportistaMain.objetivo)
        binding.etDescripPersonalShow.setText(deportistaMain.descripcionPersonal)


        binding.etNombre.setText(deportistaMain.nombre)
        binding.apellido1.setText(deportistaMain.apellido)
        if (deportistaMain.descripcionPersonal != ""){
            binding.etDescripPersonal.setText(deportistaMain.descripcionPersonal)
        }


        Glide.with(this)
            .load(deportistaMain.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imagenDeportista)

        Glide.with(this)
            .load(deportistaMain.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imageShow)

    }

    private fun requestPermission() {
        // Verificaremos el nivel de API para solicitar los permisos
        // en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
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
                requireContext(),
                "Permission denied",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data?.data
            uri_img = data
            Glide.with(this)
                .load(uri_img)
                .error(R.drawable.ic_person_white)
                .into(binding.imagenDeportista)
            imgCorrecta = true
            imagenActualizada = true
        }
    }

}