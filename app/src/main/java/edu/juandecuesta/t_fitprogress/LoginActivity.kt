package edu.juandecuesta.t_fitprogress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityLoginBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser?.email != null){
            InicioSesion(currentUser?.email!!)
            Thread.sleep(2000)
        }
        setTheme(R.style.Theme_TFitProgress_NoActionBar)

        binding.login.setOnClickListener {

            if (verificarCampos()){

                val email = binding.username.text.toString()
                val password = binding.password.text.toString()
                binding.idProgress.isVisible = true
                binding.login.setText(R.string.iniciando_sesion)


                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                    if (it.isSuccessful){
                        InicioSesion(email)
                    }else{
                        Toast.makeText(this,"Usuario y/o contraseña incorrectos",Toast.LENGTH_LONG).show()
                        limpiarCampos()
                        binding.idProgress.isVisible = false
                        binding.login.setText(R.string.sign_in)

                    }
                }
            }

        }

        binding.signUp.setOnClickListener {
            val myIntent = Intent (this, RegisterActivity::class.java)
            startActivity(myIntent)
        }

        binding.restablecerpass.setOnClickListener {
            if (TextUtils.isEmpty(binding.username.text.toString())){
                binding.tLusername.error = "Información necesaria"
            } else {
                binding.tLusername.error = null
                auth.setLanguageCode("es")
                auth.sendPasswordResetEmail(binding.username.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Se ha enviado un correo para restablecer su contraseña",Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    }else {
                        Toast.makeText(this,"No se pudo enviar el correo para restablecer contraseña",Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    }
                }
            }
        }
    }

    private fun limpiarCampos (){
        binding.username.text?.clear()
        binding.username.clearFocus()
        binding.password.text?.clear()
        binding.password.clearFocus()
    }

    override fun onStart() {
        super.onStart()
        db = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()
        limpiarErrores()
        db = FirebaseFirestore.getInstance()
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
            putExtra("esentrenador", true)
            putExtra("entrenador", entrenador)
        }
        binding.idProgress.isVisible = false
        binding.login.setText(R.string.sign_in)
        startActivity(homeIntent)
        finish()
        db.terminate()
    }

    private fun showHomeDeportista (deportista: DeportistaDB){
        val homeIntent:Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("esentrenador", false)
            putExtra("deportista", deportista)
        }
        binding.idProgress.isVisible = false
        binding.login.setText(R.string.sign_in)
        startActivity(homeIntent)
        finish()
        db.terminate()
    }



    private fun limpiarErrores(){
        binding.tLusername.error = null
        binding.tLpassword.error = null
    }

    private fun InicioSesion (email:String){
        var entrenador = EntrenadorDB()
        var deportista = DeportistaDB()
        db.collection("users").document(email).get()
            .addOnSuccessListener { doc ->
                if (doc != null){

                    if (doc.get("soyEntrenador") != null){
                        if (doc.get("soyEntrenador") as Boolean){

                            entrenador = doc.toObject(EntrenadorDB::class.java)!!
                            showHomeEntrenador(entrenador)
                        }else {

                            deportista = doc.toObject(DeportistaDB::class.java)!!

                            if (!deportista.deshabilitada){
                                showHomeDeportista(deportista)
                            }else {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                db.collection("users").document(deportista.email).delete().addOnSuccessListener {
                                    db.collection("chats").document(deportista.email).delete().addOnSuccessListener {
                                        currentUser?.delete()?.addOnCompleteListener {task ->
                                            if (task.isSuccessful) {
                                                limpiarCampos()
                                                Toast.makeText(
                                                    this,
                                                    "Cuenta eliminada, si quieres volver a acceder debes volver a registrarte.",
                                                    Toast.LENGTH_LONG
                                                ).show()
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

