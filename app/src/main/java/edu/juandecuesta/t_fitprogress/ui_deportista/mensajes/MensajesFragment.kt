package edu.juandecuesta.t_fitprogress.ui_deportista.mensajes

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.deportistaMain
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.entrenadorMain
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.DepFragmentMensajesBinding
import edu.juandecuesta.t_fitprogress.databinding.FragmentMensajesBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Chat
import edu.juandecuesta.t_fitprogress.model.Mensaje
import java.util.*


class MensajesFragment:Fragment() {
    private var _binding: DepFragmentMensajesBinding? = null
    private val binding get() = _binding!!
    private val recyclerAdapter = RecyclerAdapterMessages()
    private var mychat: Chat = Chat()
    private var yourchat: Chat = Chat()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DepFragmentMensajesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val nombre = "Entrenador ${entrenadorMain.nombre}"
        binding.txtNombreEntrenador.text = nombre

        loadRecyclerViewAdapter()
        loadOtherChat()

        binding.btnSend.setOnClickListener {

            if (!TextUtils.isEmpty(binding.txtTexto.text.toString())){
                val hoy = Calendar.getInstance()

                val fecha = "${hoy.get(Calendar.DAY_OF_MONTH)}/${hoy.get(Calendar.MONTH) + 1}/${hoy.get(
                    Calendar.YEAR)}"

                val msg = Mensaje()
                val fmtHora = Formatter()
                val hora = hoy.get(Calendar.HOUR_OF_DAY)
                fmtHora.format("%02d", hora)
                val fmtMin = Formatter()
                val min = hoy.get(Calendar.MINUTE)
                fmtMin.format("%02d", min)
                val horas = "$fmtHora:$fmtMin"
                msg.fecha = "$fecha $horas"
                msg.enviado = true
                msg.leido = true
                msg.texto = binding.txtTexto.text.toString()
                binding.txtTexto.text.clear()
                mychat.mensajes.add(0,msg)

                val yourMsg = Mensaje()
                yourMsg.texto = msg.texto
                yourMsg.fecha = msg.fecha
                yourMsg.enviado = false
                yourMsg.leido = false
                yourchat.mensajes.add(0,yourMsg)

                val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
                db.collection("chats").document(current).collection("mensajes").document(entrenadorMain.email).set(mychat).addOnSuccessListener {
                    db.collection("chats").document(entrenadorMain.email).collection("mensajes").document(current).set(yourchat)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Ha habido un error ${it.message}", Toast.LENGTH_LONG).show()
                }
            }

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpRecyclerView() {

        if (mychat.mensajes.size > 0){

            binding.rvChats.setHasFixedSize(true)

            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.stackFromEnd = true
            binding.rvChats.layoutManager = layoutManager
            recyclerAdapter.RecyclerAdapter(mychat.mensajes.asReversed(), requireContext())
            binding.rvChats.adapter = recyclerAdapter
        }

    }

    private fun loadRecyclerViewAdapter(){

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("chats").document(current).collection("mensajes")
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    mychat.mensajes.clear()
                    for (dc in doc.documentChanges){
                        if (dc.document.id == deportistaMain.entrenador){
                            when (dc.type){
                                DocumentChange.Type.ADDED -> {
                                    mychat = dc.document.toObject(Chat::class.java)

                                    mychat.mensajes.forEach { m -> m.leido = true }

                                    db.collection("chats").document(current).collection("mensajes").document(deportistaMain.entrenador).update("mensajes", mychat.mensajes)
                                    if (_binding!= null){
                                        setUpRecyclerView()
                                        recyclerAdapter.notifyDataSetChanged()
                                    }

                                }
                                DocumentChange.Type.MODIFIED -> {
                                    mychat = dc.document.toObject(Chat::class.java)

                                    mychat.mensajes.forEach { m -> m.leido = true }

                                    db.collection("chats").document(current).collection("mensajes").document(deportistaMain.entrenador).update("mensajes", mychat.mensajes)

                                    if (_binding!= null){
                                        setUpRecyclerView()
                                        recyclerAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }else continue
                    }

                }
            }
    }


    private fun loadOtherChat(){
        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("chats").document(deportistaMain.entrenador).collection("mensajes")
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    yourchat.mensajes.clear()

                    for (dc in doc.documentChanges){
                        if (dc.document.id == current) {
                            when (dc.type){
                                DocumentChange.Type.ADDED -> {
                                    yourchat = dc.document.toObject(Chat::class.java)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    yourchat = dc.document.toObject(Chat::class.java)
                                }
                            }
                        }else continue
                    }

                }
            }
    }


}