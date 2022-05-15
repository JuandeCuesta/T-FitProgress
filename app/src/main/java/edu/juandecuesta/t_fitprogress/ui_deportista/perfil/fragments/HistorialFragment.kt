package edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments

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
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.Entrenamiento_DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.MainActivity
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.databinding.FragmentHistorialDeportistaBinding
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat
import java.util.*


class HistorialFragment : Fragment() {

    private lateinit var binding: FragmentHistorialDeportistaBinding
    private val entrenamientosDep: MutableList<Entrenamiento_Deportista> = arrayListOf()
    private val recyclerAdapter = RecyclerAdapterHistorial()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHistorialDeportistaBinding.inflate(inflater)

        entrenamientosDep.clear()
        loadRecyclerViewAdapter()

        return binding.root
    }

    private fun setUpRecyclerView() {

        binding.tvInfoRvHistorial.isVisible = true

        if (entrenamientosDep.size > 0){

            binding.tvInfoRvHistorial.isVisible = false

            binding.rvHistorial.setHasFixedSize(true)
            binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())


            recyclerAdapter.RecyclerAdapter(entrenamientosDep, requireContext())
            binding.rvHistorial.adapter = recyclerAdapter
        }

    }

    private fun loadRecyclerViewAdapter(){

        db.collection("users").document(deportistaMain.email)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val deportistaDB = doc.toObject(DeportistaDB::class.java)
                    setUpRecyclerView()
                    recyclerAdapter.notifyDataSetChanged()

                    if (deportistaDB!!.entrenamientos != null) {
                        var posicion = 0;
                        for (entre:Entrenamiento_DeportistaDB in deportistaDB.entrenamientos!!){
                            val entreno = Entrenamiento_Deportista()
                            entreno.posicion = posicion
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
                                                        Entrenamiento::class.java)!!
                                                    entrenamientosDep.add(entreno)
                                                    entrenamientosDep.sortByDescending { e -> e.fechaFormat}

                                                    setUpRecyclerView()
                                                    recyclerAdapter.notifyDataSetChanged()
                                                }
                                                DocumentChange.Type.MODIFIED -> {
                                                    entreno.entrenamiento = doc.documents[0].toObject(
                                                        Entrenamiento::class.java)!!
                                                    entrenamientosDep.add(entreno)
                                                    entrenamientosDep.sortByDescending { e -> e.fechaFormat}

                                                    setUpRecyclerView()
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