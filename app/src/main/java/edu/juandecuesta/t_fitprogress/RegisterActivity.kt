package edu.juandecuesta.t_fitprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityRegisterBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenador
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import edu.juandecuesta.t_fitprogress.dialog.DatePickerFragment




class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

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

                    val email = binding.etemail.text.toString()
                    val password = binding.etpassword1.text.toString()
                    val entrenador = Entrenador()
                    entrenador.nombre = binding.etNombre.text.toString()
                    entrenador.apellido = binding.apellido1.text.toString()

                    //Creamos la autentificacion y si se crea correctamente añadimos los siguientes datos
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                        if (it.isSuccessful){
                            //Almacenamos el resto de datos, si esta correcto volvemos a la pagina de login
                            db.collection("users").document(email).set(entrenador).addOnSuccessListener {
                                Toast.makeText(this,"Entrenador creado",Toast.LENGTH_LONG).show()
                                FirebaseAuth.getInstance().signOut()
                                onBackPressed()
                            }.addOnFailureListener {
                                //Si ha habido algun error se elimina el usuario creado
                                FirebaseAuth.getInstance().currentUser?.delete()
                                    ?.addOnCompleteListener {
                                        Toast.makeText(this,"Error al crear el entrenador",Toast.LENGTH_LONG).show()
                                    }
                            }

                        }else{
                            Toast.makeText(this,"Error al crear el entrenador",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {

                if (verficarCamposDeportista() && comprobarPassword()){
                    val email = binding.etemail.text.toString()
                    val password = binding.etpassword1.text.toString()
                    val emailEntrenador = binding.etCodeEntrenador.text.toString()
                    val deportistaDB = saveDeportista(emailEntrenador)

                    db.collection("users").document(emailEntrenador).get().addOnSuccessListener { doc->
                        if (doc != null){
                            if (doc.getBoolean("soyEntrenador")!!){

                                var deportistas:MutableList<String> = arrayListOf()

                                if (doc.get("deportistas") != null){
                                    deportistas = doc.get("deportistas") as MutableList<String>
                                }

                                //Creamos deportista
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful){

                                            //Si se crea lo añadimos al entrenador
                                            deportistas.add(email)
                                            db.collection("users").document(emailEntrenador)
                                                .update("deportistas", deportistas).addOnSuccessListener {

                                                    db.collection("users").document(email).set(deportistaDB).addOnSuccessListener {
                                                        Toast.makeText(this,"Deportista creado",Toast.LENGTH_LONG).show()
                                                        FirebaseAuth.getInstance().signOut()
                                                        onBackPressed()
                                                    }.addOnFailureListener {
                                                        FirebaseAuth.getInstance().currentUser?.delete()
                                                            ?.addOnCompleteListener {
                                                                Toast.makeText(this,"Error al crear el deportista",Toast.LENGTH_LONG).show()
                                                            }
                                                    }

                                                }
                                                .addOnFailureListener { e ->  Toast.makeText(this,"Error al actualizar el entrenador",Toast.LENGTH_LONG).show()}

                                        }else{
                                            Toast.makeText(this,"Error al crear el deportista",Toast.LENGTH_LONG).show()
                                        }
                                    }

                            }else{
                                Toast.makeText(this,"Entrenador no encontrado",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
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
        if (binding.spExperiencia.selectedItemPosition == 0){
            (binding.spExperiencia.getSelectedView() as TextView).error = "Información requerida"
            valido = false
        } else (binding.spExperiencia.getSelectedView() as TextView).error = null

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
            (binding.spExperiencia.getSelectedView() as TextView).error = null
        }
    }

    private fun saveDeportista(entrenador: String):DeportistaDB{
        var deportista = DeportistaDB()
        deportista.nombre = binding.etNombre.text.toString()
        deportista.apellido = binding.apellido1.text.toString()
        deportista.fechanacimiento = binding.etEdad.text.toString()
        deportista.entrenador=entrenador
        deportista.sexo = if (binding.rbHombre.isChecked) "Hombre" else "Mujer"
        deportista.experiencia = binding.spExperiencia.selectedItem.toString()

        return deportista
    }

}