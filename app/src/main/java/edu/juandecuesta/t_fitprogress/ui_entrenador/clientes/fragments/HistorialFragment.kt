package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.FragmentHistorialBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.home.RecyclerAdapterHomeDeportista
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat
import java.util.*


class HistorialFragment : Fragment() {

    private val entrenamientosDep: MutableList<Entrenamiento_Deportista> = arrayListOf()
    private val recyclerAdapter = RecyclerAdapterHomeDeportista()
    private var _binding: FragmentHistorialBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistorialBinding.inflate(inflater)
        val root: View = binding.root

        entrenamientosDep.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()

        return root
    }

    override fun onResume() {
        super.onResume()
        //entrenamientosDep.clear()
        //setUpRecyclerView()
        //loadRecyclerViewAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRvHistorial.isVisible = true

        if (MainActivity.entrenadorMain.entrenamientos.size > 0){

            binding.tvInfoRvHistorial.isVisible = false

            binding.rvHistorial.setHasFixedSize(true)
            binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())


            recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
            binding.rvHistorial.adapter = recyclerAdapter
        }

    }


    private fun loadRecyclerViewAdapter(){

        try {
            db.collection("users").whereEqualTo(FieldPath.documentId(),deportista.email)
                .addSnapshotListener { document, except ->
                    if (except != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", except)
                        return@addSnapshotListener
                    }

                    if (document != null) {
                        val deportistaDB = document.documents[0].toObject(DeportistaDB::class.java)

                        for (dc in document.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    if (deportistaDB != null) {
                                        mostrarentrenos(deportistaDB)
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {

                                    entrenamientosDep.clear()
                                    if (deportistaDB != null) {
                                        mostrarentrenos(deportistaDB)
                                    }
                                }
                                else -> {}
                            }
                        }


                    }
                }

        }catch (e:Exception){
            e.message?.let { Log.d("HISTORIAL", it) }
        }

    }

    private fun mostrarentrenos(deportistaDB: DeportistaDB){
        if (deportistaDB.entrenamientos != null) {

            var posicion = 0
            for (entre in deportistaDB.entrenamientos!!) {
                val entreno = Entrenamiento_Deportista()
                entreno.posicion = posicion
                entreno.prueba = entre.prueba
                posicion++
                entreno.deportista = deportistaDB
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                entreno.fechaFormat = sdf.parse(entre.fecha)

                if (Functions().calcularFecha(entre.fecha) == 0){
                    entreno.fecha = "Hoy - ${entre.fecha}"
                } else if (Functions().calcularFecha(entre.fecha) > 0){
                    entreno.fecha = entre.fecha
                }else{
                    continue
                }

                entreno.realizado = entre.realizado

                db.collection("entrenamientos").whereEqualTo(
                    FieldPath.documentId(),
                    entre.entrenamiento
                ).addSnapshotListener { documento, excepcion ->
                    if (excepcion != null) {
                        Log.w(
                            ContentValues.TAG,
                            "Listen failed.",
                            excepcion
                        )
                        return@addSnapshotListener
                    }

                    if (documento != null) {
                        for (dc in documento.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    entreno.entrenamiento =
                                        documento.documents[0].toObject(
                                            Entrenamiento::class.java
                                        )!!
                                    entrenamientosDep.add(entreno)
                                    entrenamientosDep.sortByDescending { e -> e.fechaFormat }

                                    if (_binding != null) {
                                        binding.tvInfoRvHistorial.isVisible =
                                            false
                                        recyclerAdapter.RecyclerAdapter(
                                            entrenamientosDep,
                                            requireContext()
                                        )
                                        recyclerAdapter.notifyDataSetChanged()
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    entreno.entrenamiento = documento.documents[0].toObject(
                                        Entrenamiento::class.java)!!
                                    for (i in 0 until entrenamientosDep.size){
                                        if (entrenamientosDep[i].entrenamiento.id == entreno.entrenamiento.id){
                                            entrenamientosDep.set(i,entreno)
                                            entrenamientosDep.sortByDescending { e -> e.fechaFormat}
                                        }
                                    }
                                    if (_binding != null){
                                        recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
                                        recyclerAdapter.notifyDataSetChanged()
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                }
            }
        }
    }



}