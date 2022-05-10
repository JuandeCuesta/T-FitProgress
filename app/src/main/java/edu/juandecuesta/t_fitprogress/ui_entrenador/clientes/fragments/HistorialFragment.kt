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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentHistorialBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.ui_entrenador.clientes.ShowClientActivity.Companion.deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos.RecyclerAdapterEntrenamientos
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat
import java.util.*


class HistorialFragment : Fragment() {

    private lateinit var binding: FragmentHistorialBinding
    private val entrenamientosDep: MutableList<Entrenamiento_Deportista> = arrayListOf()
    private val recyclerAdapter = RecyclerAdapterHistorial()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHistorialBinding.inflate(inflater)

        entrenamientosDep.clear()
        setUpRecyclerView()
        loadRecyclerViewAdapter()

        return binding.root
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRvHistorial.isVisible = true

        if (MainActivity.entrenador.entrenamientos.size > 0){

            binding.tvInfoRvHistorial.isVisible = false

            binding.rvHistorial.setHasFixedSize(true)
            binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())


            recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
            binding.rvHistorial.adapter = recyclerAdapter
        }

    }

    private fun loadRecyclerViewAdapter(){

        db.collection("users").document(deportista.email)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val deportistaDB = doc.toObject(DeportistaDB::class.java)
                    binding.tvInfoRvHistorial.isVisible = true
                    entrenamientosDep.clear()
                    recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
                    recyclerAdapter.notifyDataSetChanged()

                    if (deportistaDB!!.entrenamientos != null) {
                        for (entre:Entrenamiento_DeportistaDB in deportistaDB.entrenamientos!!){

                            val entreno = Entrenamiento_Deportista()
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

                            binding.tvInfoRvHistorial.isVisible = false
                            db.collection("entrenamientos").whereEqualTo(FieldPath.documentId(),entre.entrenamiento)
                                .addSnapshotListener{doc, exc ->
                                    if (exc != null){
                                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                                        return@addSnapshotListener
                                    }

                                    if (doc != null){
                                        for (dc in doc.documentChanges){
                                            when (dc.type){
                                                DocumentChange.Type.ADDED -> {
                                                    entreno.entrenamiento = doc.documents[0].toObject(
                                                        Entrenamiento::class.java)
                                                    entrenamientosDep.add(entreno)
                                                    entrenamientosDep.sortByDescending { e -> e.fechaFormat}

                                                    recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
                                                    recyclerAdapter.notifyDataSetChanged()
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
    }

}