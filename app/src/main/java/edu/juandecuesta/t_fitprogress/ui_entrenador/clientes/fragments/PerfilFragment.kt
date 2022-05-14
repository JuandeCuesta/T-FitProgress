package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentPerfilBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.utils.Functions



class PerfilFragment : Fragment() {

    private lateinit var binding:FragmentPerfilBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPerfilBinding.inflate(inflater)
        val root: View = binding.root

        binding.btnEliminarPerfil.setOnClickListener {
            alerta()
        }

        cargardatos()

        return root
    }

    private fun alerta(){
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("Eliminar perfil")
            setMessage("No será posible recuperar la cuenta, ¿estás seguro?")
            setPositiveButton(
                android.R.string.ok){_,_-> eliminarcuenta() }
            setNegativeButton(
                android.R.string.cancel){_,_->}
        }
        builder.show()
    }

    private fun eliminarcuenta() {

        deportista.deshabilitada = true
        db.collection("users").document(deportista.email).set(deportista).addOnSuccessListener {
            db.collection("users").document(deportista.entrenador).get().addOnSuccessListener { doc->
                val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                entrenadorDb?.deportistas?.remove(deportista.email)
                if (entrenadorDb != null) {
                    db.collection("users").document(deportista.entrenador).update("deportistas", entrenadorDb.deportistas).addOnSuccessListener {

                        db.collection("chats").document(deportista.entrenador).collection("mensajes").document(deportista.email).delete()

                        Toast.makeText(requireContext(), "El deportista ha sido deshabilitado.", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    private fun backpressed() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            if (!findNavController().navigateUp()) {
                if (isEnabled) {
                    isEnabled = false
                }
            }
        }
    }


    fun cargardatos() {

        val array = requireContext().resources.getStringArray(R.array.material_calendar_months_array)

        val nombreCompleto = "${deportista.nombre} ${deportista.apellido}"
        val dia = deportista.fechacreacion.split("/")[0]
        val mes = deportista.fechacreacion.split("/")[1].toInt()
        val anyo = deportista.fechacreacion.split("/")[2].toInt()
        val fechaRegistro = "$dia de ${array[mes-1]} de $anyo"

        val diaB = deportista.fechanacimiento.split("/")[0]
        val mesB = deportista.fechanacimiento.split("/")[1].toInt()
        val anyoB = deportista.fechanacimiento.split("/")[2].toInt()
        val fechaNacimiento = "$diaB de ${array[mesB-1]} de $anyoB"

        binding.nombreShow.text = nombreCompleto
        binding.etFCreacion.setText(fechaRegistro)
        binding.etEmailDep.setText(deportista.email)
        binding.etFNacimiento.setText(fechaNacimiento)
        binding.etSexo.setText(deportista.sexo)
        binding.etExperiencia.setText(deportista.experiencia)
        binding.etObjetivosShow.setText(deportista.objetivo)
        binding.etDescripPersonalShow.setText(deportista.descripcionPersonal)

        Glide.with(this)
            .load(deportista.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imageShow)

    }

}