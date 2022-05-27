package edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentMensajesBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Chat

import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isVisible
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.db
import edu.juandecuesta.t_fitprogress.MainActivity.Companion.searchView
import edu.juandecuesta.t_fitprogress.utils.Functions


class MensajesFragment:Fragment() {
    private val recyclerAdapter = RecyclerAdapterChat()
    private var chats: MutableList<Chat> = arrayListOf()
    private var deportistas: MutableList<DeportistaDB> = arrayListOf()
    private var _binding: FragmentMensajesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMensajesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        cargarClientes()
        loadDeportistaChat()
        setHasOptionsMenu(true)

        binding.floatingActionButton.setOnClickListener {
            if (deportistas.size > 0){
                dialog()
            } else {
                Toast.makeText(requireContext(), "Aún no tienes clientes con quién chatear", Toast.LENGTH_LONG).show()
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun dialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        val items = arrayOfNulls<CharSequence>(deportistas.size)
        for (i in 0 until deportistas.size){
            val nombreCompleto = "${deportistas[i].nombre} ${deportistas[i].apellido}"
            items[i] = nombreCompleto

        }

        builder.setTitle("Seleccionar deportista")
            .setItems(items, DialogInterface.OnClickListener { dialog, index ->
                val messageIntent = Intent (context, CreateMessageActivity::class.java).apply {
                    putExtra("deportista", deportistas[index])
                }
                startActivity(messageIntent)
            })

        builder.show()
    }

    private fun cargarClientes(){

        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("users").document(current)
            .addSnapshotListener{ doc, exc ->
                if (exc != null){
                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                    return@addSnapshotListener
                }

                if (doc != null){
                    val entrenadorDb = doc.toObject(EntrenadorDB::class.java)
                    deportistas.clear()

                    for (emailDep:String in entrenadorDb?.deportistas!!){
                        db.collection("users").whereEqualTo(FieldPath.documentId(),emailDep)
                            .addSnapshotListener{doc, exc ->
                                if (exc != null){
                                    Log.w(ContentValues.TAG, "Listen failed.", exc)
                                    return@addSnapshotListener
                                }

                                if (doc != null){
                                    for (dc in doc.documentChanges){
                                        when (dc.type){
                                            DocumentChange.Type.ADDED -> {
                                                val deportistaDB = doc.documents[0].toObject(
                                                    DeportistaDB::class.java)
                                                deportistas.add(deportistaDB!!)
                                            }
                                            DocumentChange.Type.MODIFIED -> {
                                                val deportistaDB = doc.documents[0].toObject(
                                                    DeportistaDB::class.java)
                                                for (i in 0 until deportistas.size){
                                                    if (deportistas[i].email == deportistaDB!!.email){
                                                        deportistas[i] = deportistaDB
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
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()

        // Add the new menu items
        inflater.inflate(R.menu.main, menu)

        val search = menu?.findItem(R.id.app_bar_search)
        searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                recyclerAdapter.filter(text!!)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView() {

        binding.tvSinChats.isVisible = true

        if (chats.size > 0){
            binding.tvSinChats.isVisible = false
            binding.rvChats.setHasFixedSize(true)
            binding.rvChats.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapter.RecyclerAdapter(chats, requireContext())
            binding.rvChats.adapter = recyclerAdapter
            var dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            binding.rvChats.addItemDecoration(dividerItemDecoration)
        }

    }

    private fun loadDeportistaChat (){
        val current = FirebaseAuth.getInstance().currentUser?.email ?: ""
        db.collection("chats").document(current).collection("mensajes").get().addOnSuccessListener {docs ->

            chats.clear()
            setUpRecyclerView()
            recyclerAdapter.notifyDataSetChanged()

            for (doc in docs){
                db.collection("chats").document(current).collection("mensajes").whereEqualTo(FieldPath.documentId(), doc.id).addSnapshotListener { it, error ->
                    if (error != null){
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (it != null){
                        for (c in it.documentChanges){
                            when(c.type){
                                DocumentChange.Type.ADDED -> {
                                    val chat = c.document.toObject(Chat::class.java)
                                    val email = c.document.id
                                    db.collection("users").whereEqualTo(FieldPath.documentId(),email).addSnapshotListener { doc, exc ->
                                        if (exc != null){
                                            Log.w(ContentValues.TAG, "Listen failed.", exc)
                                            return@addSnapshotListener
                                        }
                                        if (doc != null){
                                            for (dc in doc.documentChanges){
                                                when (dc.type){
                                                    DocumentChange.Type.ADDED -> {
                                                        if (_binding!=null){
                                                            val deportistaDB = dc.document.toObject(
                                                                DeportistaDB::class.java)
                                                            chat.deportista = deportistaDB
                                                            chats.add(chat)
                                                            chats.sortByDescending { c -> Functions().formatearFechayHora(c.mensajes.first().fecha) }
                                                            setUpRecyclerView()
                                                            recyclerAdapter.notifyDataSetChanged()
                                                        }
                                                    }
                                                    DocumentChange.Type.MODIFIED -> {
                                                        if (_binding!=null){
                                                            val deportistaDB = dc.document.toObject(
                                                                DeportistaDB::class.java)
                                                            chats.forEach { c ->
                                                                if (c.deportista.email == deportistaDB.email){
                                                                    c.deportista = deportistaDB
                                                                }
                                                            }
                                                            setUpRecyclerView()
                                                            recyclerAdapter.notifyDataSetChanged()
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    if (_binding!=null){
                                        val chat = c.document.toObject(Chat::class.java)
                                        val email = c.document.id
                                        for (i in 0 until chats.size){
                                            if (chats[i].deportista.email == email){
                                                chats[i].mensajes = chat.mensajes
                                                chats.sortByDescending { c -> Functions().formatearFechayHora(c.mensajes.first().fecha) }
                                            }
                                        }
                                        setUpRecyclerView()
                                        recyclerAdapter.notifyDataSetChanged()
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