package edu.juandecuesta.t_fitprogress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.databinding.ActivityLoginBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Entrenador
import edu.juandecuesta.t_fitprogress.utils.Functions

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        var entrenador = EntrenadorDB()
        val deportista = DeportistaDB()

        if (currentUser?.email != null){
            db.collection("users").document(currentUser.email!!).get()
                .addOnSuccessListener { doc ->
                    if (doc != null){

                        if (doc.get("soyEntrenador") != null && doc.get("soyEntrenador") as Boolean){

                            entrenador = Functions().loadEntrenador(doc)

                            showHomeEntrenador(entrenador)
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

                                    entrenador = Functions().loadEntrenador(doc)

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

    private fun showHomeEntrenador (entrenador: EntrenadorDB){
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

