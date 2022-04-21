package edu.juandecuesta.t_fitprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

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


                    Toast.makeText(this,"Entrenador creado",Toast.LENGTH_LONG).show()

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
}