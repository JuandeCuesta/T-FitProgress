package edu.juandecuesta.t_fitprogress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityLoginBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser?.email != null){
            InicioSesion(currentUser?.email!!)
        }

        binding.login.setOnClickListener {

            if (verificarCampos()){

                val email = binding.username.text.toString()
                val password = binding.password.text.toString()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                    if (it.isSuccessful){
                        InicioSesion(email)
                    }else{
                        Toast.makeText(this,"Error al iniciar sesión",Toast.LENGTH_LONG).show()
                        limpiarCampos()

                    }
                }
            }

        }

        binding.signUp.setOnClickListener {
            val myIntent = Intent (this, RegisterActivity::class.java)
            startActivity(myIntent)
        }

    }

    private fun limpiarCampos (){
        binding.username.text?.clear()
        binding.username.clearFocus()
        binding.password.text?.clear()
        binding.password.clearFocus()
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
            putExtra("esentrenador", true)
            putExtra("entrenador", entrenador)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showHomeDeportista (deportista: DeportistaDB){
        val homeIntent:Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("esentrenador", false)
            putExtra("deportista", deportista)
        }
        startActivity(homeIntent)
        finish()
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
                                currentUser?.delete()?.addOnCompleteListener {task ->
                                    if (task.isSuccessful) {
                                        db.collection("users").document(deportista.email).delete().addOnSuccessListener {
                                            db.collection("chats").document(deportista.email).delete()
                                            limpiarCampos ()
                                            Toast.makeText(this,"Cuenta eliminada, si quieres volver a acceder debes volver a registrarte.",Toast.LENGTH_LONG).show()
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

