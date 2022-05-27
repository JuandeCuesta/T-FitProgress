package edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.databinding.ActivityCreateMessageBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Chat
import edu.juandecuesta.t_fitprogress.model.Mensaje
import java.util.*

class CreateMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateMessageBinding
    private val recyclerAdapter = RecyclerAdapterMessages()
    private var mychat: Chat = Chat()
    private var yourchat: Chat = Chat()
    private var deportista = DeportistaDB()
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.deportista = intent.getSerializableExtra("deportista") as DeportistaDB
        val nombreCompleto = (deportista.nombre + " " + deportista.apellido)
        title = nombreCompleto

        loadRecyclerViewAdapter()
        loadOtherChat()

        binding.txtTexto.addTextChangedListener{
            if (it.toString() != ""){
                binding.btnSend.isClickable = true
            }
        }

        binding.btnSend.setOnClickListener {
            val hoy = Calendar.getInstance()

            val fecha = "${hoy.get(Calendar.DAY_OF_MONTH)}/${hoy.get(Calendar.MONTH) + 1}/${hoy.get(Calendar.YEAR)}"

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
            yourchat.deportista = deportista

            val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
            db.collection("chats").document(current).collection("mensajes").document(deportista.email).set(mychat).addOnSuccessListener {
                db.collection("chats").document(deportista.email).collection("mensajes").document(current).set(yourchat)
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setUpRecyclerView() {

        if (mychat.mensajes.size > 0){

            binding.rvChats.setHasFixedSize(true)

            val layoutManager = LinearLayoutManager(this)
            layoutManager.stackFromEnd = true
            binding.rvChats.layoutManager = layoutManager
            recyclerAdapter.RecyclerAdapter(mychat.mensajes.asReversed(), this)
            binding.rvChats.adapter = recyclerAdapter
        }

    }

    private fun loadRecyclerViewAdapter(){

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        if (deportista.email != ""){
            db.collection("chats").document(current).collection("mensajes").whereEqualTo(FieldPath.documentId(), deportista.email)
                .addSnapshotListener{ doc, exc ->
                    if (exc != null){
                        Log.w(ContentValues.TAG, "Listen failed.", exc)
                        return@addSnapshotListener
                    }

                    if (doc != null){
                        for (dc in doc.documentChanges){
                            if (dc.document.id == deportista.email){
                                when (dc.type){
                                    DocumentChange.Type.ADDED -> {
                                        if (deportista.email != ""){
                                            mychat = dc.document.toObject(Chat::class.java)
                                            mychat.mensajes.forEach { m -> m.leido = true }
                                            db.collection("chats").document(current).collection("mensajes").document(deportista.email).update("mensajes", mychat.mensajes)
                                            setUpRecyclerView()
                                            recyclerAdapter.notifyDataSetChanged()
                                        }
                                    }
                                    DocumentChange.Type.MODIFIED -> {

                                        if (deportista.email != ""){
                                            mychat = dc.document.toObject(Chat::class.java)
                                            mychat.mensajes.forEach { m -> m.leido = true }
                                            db.collection("chats").document(current).collection("mensajes").document(deportista.email).update("mensajes", mychat.mensajes)
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

    }

    override fun onBackPressed() {
        deportista = DeportistaDB()
        super.onBackPressed()

    }

    private fun loadOtherChat(){

        db.collection("chats").document(deportista.email).collection("mensajes")
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    //yourchat.mensajes.clear()
                    for (dc in doc.documentChanges){
                        when (dc.type){
                            DocumentChange.Type.ADDED -> {
                                yourchat = dc.document.toObject(Chat::class.java)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                yourchat = dc.document.toObject(Chat::class.java)
                            }
                        }
                    }

                }
            }
    }
}