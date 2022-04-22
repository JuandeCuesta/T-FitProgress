package edu.juandecuesta.t_fitprogress

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.databinding.ActivityLoginBinding
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.model.Entrenador
import edu.juandecuesta.t_fitprogress.model.Entrenamiento

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val entrenador = Entrenador()


        if (currentUser != null){

            currentUser.email?.let {
                db.collection("users").document(it).get()
                    .addOnSuccessListener { doc ->
                        if (doc != null){

                            if (doc.get("soyEntrenador") as Boolean){

                                entrenador.id = doc.get("id") as String
                                entrenador.nombre = doc.get("nombre") as String
                                entrenador.apellido = doc.get("apellido") as String
                                entrenador.email = it
                                entrenador.soyEntrenador = doc.get("soyEntrenador") as Boolean

                                if (doc.get("deportistas") != null){
                                    entrenador.deportistas = doc.get("deportistas") as MutableList<Deportista>
                                }

                                if (doc.get("ejercicios") != null){
                                    entrenador.ejercicios = doc.get("ejercicios") as MutableList<Ejercicio>
                                }

                                if (doc.get("entrenamientos") != null){
                                    entrenador.entrenamientos = doc.get("entrenamientos") as MutableList<Entrenamiento>
                                }

                                showHomeEntrenador(entrenador)
                            }

                        }
                    }
            }
        }

        binding.login.setOnClickListener {

            if (verificarCampos()){

                val email = binding.username.text.toString()
                val password = binding.password.text.toString()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                    if (it.isSuccessful){

                        db.collection("users").document(email).get()
                        .addOnSuccessListener { doc ->
                            if (doc != null){
                                if (doc.get("soyEntrenador") as Boolean){

                                    entrenador.id = doc.get("id") as String
                                    entrenador.nombre = doc.get("nombre") as String
                                    entrenador.apellido = doc.get("apellido") as String
                                    entrenador.email = email
                                    entrenador.soyEntrenador = doc.get("soyEntrenador") as Boolean

                                    if (doc.get("deportistas") != null){
                                        entrenador.deportistas = doc.get("deportistas") as MutableList<Deportista>
                                    }

                                    if (doc.get("ejercicios") != null){
                                        entrenador.ejercicios = doc.get("ejercicios") as MutableList<Ejercicio>
                                    }

                                    if (doc.get("entrenamientos") != null){
                                        entrenador.entrenamientos = doc.get("entrenamientos") as MutableList<Entrenamiento>
                                    }

                                    showHomeEntrenador(entrenador)
                                }

                            }
                        }

                    }else{
                        Toast.makeText(this,"Error al autentificar el usuario",Toast.LENGTH_LONG).show()
                        binding.username.text?.clear()
                        binding.username.clearFocus()
                        binding.password.text?.clear()
                        binding.password.clearFocus()

                    }
                }
            }

        }

        binding.signUp.setOnClickListener {
            val myIntent = Intent (this, RegisterActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        limpiarErrores()
    }

    private fun verificarCampos():Boolean{
        var valido = true

        if (TextUtils.isEmpty(binding.username.text.toString())){
            binding.tLusername.error = "Información requerida"
            valido = false
        } else binding.tLusername.error = null

        if (TextUtils.isEmpty(binding.password.text.toString())){
            binding.tLpassword.error = "Información requerida"
            valido = false
        } else binding.tLpassword.error = null

        return valido
    }

    private fun showHomeEntrenador (entrenador: Entrenador){
        val homeIntent:Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("entrenador", entrenador)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun limpiarErrores(){
        binding.tLusername.error = null
        binding.tLpassword.error = null
    }
}

