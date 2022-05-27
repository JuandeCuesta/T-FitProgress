package edu.juandecuesta.t_fitprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityRegisterBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB

import android.app.DatePickerDialog
import android.widget.ArrayAdapter

import edu.juandecuesta.t_fitprogress.ui_entrenador.dialogAddEntrenamiento.DatePickerFragment
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.utils.Functions


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db:FirebaseFirestore
    var ejerciciosDefecto:MutableList<Ejercicio> = arrayListOf()
    val entrenador = EntrenadorDB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = arrayOf("Ninguno", "Bajo", "Medio", "Alto")

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, type)
        binding.etExperiencia.setAdapter(adapter)

        binding.rbDeportista.setOnClickListener {
            if (binding.rbDeportista.isChecked){
                binding.linearLayout2.isVisible = true
            }
        }
        binding.rbEntrenador.setOnClickListener {
            if (binding.rbEntrenador.isChecked){
                binding.linearLayout2.isVisible = false
            }
        }

        binding.etEdad.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnRegistrar.setOnClickListener {
            vaciarErrores()
            if (binding.rbEntrenador.isChecked){
                if (verificarCamposEntrenador() && comprobarPassword()){
                    binding.lyProgress.isVisible = true
                    binding.btnRegistrar.isVisible = false
                    val email = binding.etemail.text.toString()
                    val password = binding.etpassword1.text.toString()
                    entrenador.nombre = binding.etNombre.text.toString()
                    entrenador.apellido = binding.apellido1.text.toString()
                    entrenador.email = email
                    //Creamos la autentificacion y si se crea correctamente añadimos los siguientes datos
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                cargarEjercicios()

                            }else{
                                Functions().showSnackSimple(binding.root, "El entrenador no ha podido ser registrado, ya existe una cuenta con ese email.")
                                eliminarEjercicios()
                                binding.lyProgress.isVisible = false
                                binding.btnRegistrar.isVisible = true
                            }
                        }

                }
            }
            else {
                if (verficarCamposDeportista() && comprobarPassword()){
                    binding.lyProgress.isVisible = true
                    binding.btnRegistrar.isVisible = false
                    val email = binding.etemail.text.toString()
                    val password = binding.etpassword1.text.toString()
                    val emailEntrenador = binding.etCodeEntrenador.text.toString()
                    val deportistaDB = saveDeportista(emailEntrenador)

                    db.collection("users").document(emailEntrenador).get().addOnSuccessListener { doc->
                        if (doc.exists()){
                            if (doc.getBoolean("soyEntrenador")!!){

                                var deportistas:MutableList<String> = arrayListOf()

                                if (doc.get("deportistas") != null){
                                    deportistas = doc.get("deportistas") as MutableList<String>
                                }

                                //Creamos deportista
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful){
                                            db.collection("users").document(email).set(deportistaDB).addOnSuccessListener {

                                                //Si se crea lo añadimos al entrenador
                                                deportistas.add(email)
                                                db.collection("users").document(emailEntrenador)
                                                    .update("deportistas", deportistas).addOnSuccessListener {
                                                        Toast.makeText(this, "El deportista ha sido registrado con éxito.", Toast.LENGTH_LONG).show()
                                                        FirebaseAuth.getInstance().signOut()
                                                        onBackPressed()
                                                        db.terminate()

                                                    }.addOnFailureListener { e ->
                                                        db.collection("users").document(email).delete().addOnSuccessListener {
                                                            FirebaseAuth.getInstance().currentUser?.delete()
                                                                ?.addOnCompleteListener {
                                                                    Functions().showSnackSimple(binding.root, "No existe entrenador con ese email registrado en la base de datos.")
                                                                    binding.lyProgress.isVisible = false
                                                                    binding.btnRegistrar.isVisible = true
                                                                }
                                                        }
                                                    }
                                            }.addOnFailureListener {
                                                FirebaseAuth.getInstance().currentUser?.delete()
                                                    ?.addOnCompleteListener {
                                                        Functions().showSnackSimple(binding.root, "Ha habido un error a la hora de registrar al deportista.")
                                                        binding.lyProgress.isVisible = false
                                                        binding.btnRegistrar.isVisible = true
                                                    }
                                            }
                                        }else{
                                            Functions().showSnackSimple(binding.root, "El deportista no ha podido ser registrado, ya existe una cuenta con ese email.")
                                            binding.lyProgress.isVisible = false
                                            binding.btnRegistrar.isVisible = true
                                        }
                                    }

                            }else{
                                Functions().showSnackSimple(binding.root, "Entrenador con email $emailEntrenador no encontrado en la base de datos")
                                binding.lyProgress.isVisible = false
                                binding.btnRegistrar.isVisible = true
                            }
                        }else{
                            Functions().showSnackSimple(binding.root, "Entrenador con email $emailEntrenador no encontrado en la base de datos")
                            binding.lyProgress.isVisible = false
                            binding.btnRegistrar.isVisible = true
                        }
                    }
                }
            }


        }
    }

    private fun eliminarEjercicios() {
        for (e in ejerciciosDefecto){
            db.collection("ejercicios").document(e.id).delete()
        }
    }

    private fun guardarEntrenador(entrenador: EntrenadorDB) {
        //Almacenamos el resto de datos, si esta correcto volvemos a la pagina de login
        db.collection("users").document(entrenador.email).set(entrenador).addOnSuccessListener {
            Toast.makeText(this, "El entrenador ha sido registrado con éxito.", Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            db.terminate()
        }.addOnFailureListener {
            //Si ha habido algun error se elimina el usuario creado
            FirebaseAuth.getInstance().currentUser?.delete()
                ?.addOnCompleteListener {
                    Functions().showSnackSimple(binding.root, "El entrenador no ha podido ser registrado, ya existe una cuenta con ese email.")
                }
        }
    }

    private fun cargarEjercicios(){
        db.collection("ejerciciosdefecto").get().addOnSuccessListener {
            docs ->
            val ejercicios = docs.toObjects(Ejercicio::class.java) as MutableList<Ejercicio>

            for (e in ejercicios){
                db.collection("ejercicios").add(e).addOnSuccessListener {doc ->
                    db.collection("ejercicios").document(doc.id).update("id", doc.id).addOnSuccessListener {
                        ejerciciosDefecto.add(e)
                        entrenador.ejercicios.add(doc.id)
                        if (entrenador.ejercicios.size == docs.documents.size){
                            guardarEntrenador(entrenador)
                        }
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->

            var dia = ""
            var mes = ""

            if (day < 10){
                dia = "0${day}"
            }else {
                dia = "$day"
            }

            if ((month + 1) < 10){
                mes = "0${month + 1}"
            }else {
                mes = "${month + 1}"
            }


            val selectedDate = "$dia/$mes/$year"
            binding.etEdad.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    //Método para verificar que se han introducido los campos de nombre, descripción y url para poder avanzar
    private fun verificarCamposEntrenador():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.etNombre.text.toString())){
            binding.tLnombre.error = "Información requerida"
            valido = false
        } else binding.tLnombre.error = null

        if (TextUtils.isEmpty(binding.etemail.text.toString())){
            binding.tLemail.error = "Información requerida"
            valido = false
        } else binding.tLemail.error = null

        if (TextUtils.isEmpty(binding.etpassword1.text.toString())){
            binding.tLpassword1.error = "Información requerida"
            valido = false
        } else binding.tLpassword2.error = null

        if (TextUtils.isEmpty(binding.etpassword2.text.toString())){
            binding.tLpassword2.error = "Información requerida"
            valido = false
        } else binding.tLpassword2.error = null

        return valido
    }

    private fun verficarCamposDeportista():Boolean{
        var valido = verificarCamposEntrenador()

        if (TextUtils.isEmpty(binding.etCodeEntrenador.text.toString())){
            binding.tLcodeEntrenador.error = "Información requerida"
            valido = false
        } else binding.tLcodeEntrenador.error = null

        if (TextUtils.isEmpty(binding.etEdad.text.toString())){
            binding.tLedad.error = "Información requerida"
            valido = false
        } else binding.tLedad.error = null

        if (TextUtils.isEmpty(binding.etExperiencia.text.toString())){
            binding.tlExperiencia.error = "Información requerida"
            valido = false
        } else binding.tlExperiencia.error = null

        return valido
    }

    private fun comprobarPassword():Boolean{

        if (TextUtils.equals(binding.etpassword1.text.toString(),binding.etpassword2.text.toString())){
            binding.tLpassword1.error = null
            binding.tLpassword2.error = null
            return true
        }
        binding.tLpassword1.error = "Las contraseñas deben coincidir"
        binding.tLpassword2.error = "Las contraseñas deben coincidir"
        return false
    }

    private fun vaciarErrores(){
        binding.tLnombre.error = null
        binding.tLemail.error = null
        binding.tLpassword1.error = null
        binding.tLpassword2.error = null

        if (binding.rbDeportista.isChecked){
            binding.tLcodeEntrenador.error = null
            binding.tLedad.error = null
            binding.tlExperiencia.error = null
        }
    }

    private fun saveDeportista(entrenador: String): DeportistaDB {
        var deportista = DeportistaDB()
        deportista.nombre = binding.etNombre.text.toString()
        deportista.apellido = binding.apellido1.text.toString()
        deportista.fechanacimiento = binding.etEdad.text.toString()
        deportista.email = binding.etemail.text.toString()
        deportista.entrenador=entrenador
        deportista.sexo = if (binding.rbHombre.isChecked) "Hombre" else "Mujer"
        deportista.experiencia = binding.etExperiencia.text.toString()
        deportista.fechacreacion = Functions().mostrarFecha()

        return deportista
    }

}