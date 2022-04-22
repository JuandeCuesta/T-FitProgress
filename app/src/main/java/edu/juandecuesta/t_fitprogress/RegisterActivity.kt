package edu.juandecuesta.t_fitprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityRegisterBinding
import edu.juandecuesta.t_fitprogress.model.Entrenador

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

        binding.btnRegistrar.setOnClickListener {

            vaciarErrores()

            if (binding.rbEntrenador.isChecked){

                if (verificarCamposEntrenador() && comprobarPassword()){

                    val email = binding.etemail.text.toString()
                    val password = binding.etpassword1.text.toString()

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this,"Entrenador creado",Toast.LENGTH_LONG).show()
                            it.result.user?.let { it1 -> saveEntrenador(email, it1.uid) }
                            onBackPressed()
                        }else{
                            Toast.makeText(this,"Error al crear el entrenador",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
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
    }

    private fun saveEntrenador(email:String, id:String){
        var entrenador = Entrenador()
        entrenador.nombre = binding.etNombre.text.toString()
        entrenador.apellido = binding.apellido1.text.toString()
        entrenador.email = email
        entrenador.id = id
        entrenador.soyEntrenador = true

        db.collection("users").document(email).set(entrenador)
    }
}