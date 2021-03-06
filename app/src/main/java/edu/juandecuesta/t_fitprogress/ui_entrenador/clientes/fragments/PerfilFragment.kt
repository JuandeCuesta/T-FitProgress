package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentPerfilBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista


class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPerfilBinding.inflate(inflater)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        db.collection("users").whereEqualTo(FieldPath.documentId(), deportista.email).addSnapshotListener { value, error ->

            if (error != null) {
                Log.w(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (value != null) {
                for (dc in value.documentChanges){
                    when(dc.type){
                        DocumentChange.Type.ADDED -> {
                            val deportistaDB = dc.document.toObject(DeportistaDB::class.java)
                            if (_binding != null){
                                mostrardatos(deportistaDB)
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val deportistaDB = dc.document.toObject(DeportistaDB::class.java)
                            if (_binding != null){
                                mostrardatos(deportistaDB)
                            }
                        }

                    }
                }
            }

        }


    }

    private fun mostrardatos(deportista: DeportistaDB) {
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
        if (deportista.descripcionPersonal == ""){
            binding.etDescripPersonalShow.text?.clear()
        } else binding.etDescripPersonalShow.setText(deportista.descripcionPersonal)

        Glide.with(this)
            .load(deportista.urlImagen)
            .error(R.drawable.ic_person_white)
            .into(binding.imageShow)
    }

}