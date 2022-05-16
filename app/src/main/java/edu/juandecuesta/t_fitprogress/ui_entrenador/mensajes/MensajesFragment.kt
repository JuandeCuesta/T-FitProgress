package edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.FragmentMensajesBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.documentFirebase.EntrenadorDB
import edu.juandecuesta.t_fitprogress.model.Chat

import android.content.DialogInterface
import android.content.Intent
import edu.juandecuesta.t_fitprogress.databinding.EntFragmentClientesBinding


class MensajesFragment:Fragment() {
    private lateinit var mensajesViewModel: MensajesViewModel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
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
        mensajesViewModel =
            ViewModelProvider(this).get(MensajesViewModel::class.java)

        _binding = FragmentMensajesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        cargarClientes()
        loadRecyclerViewAdapter()
        setHasOptionsMenu(true)

        binding.floatingActionButton.setOnClickListener {
            dialog()
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
                                    deportistas.clear()
                                    for (dc in doc.documentChanges){
                                        when (dc.type){
                                            DocumentChange.Type.ADDED -> {
                                                val deportistaDB = doc.documents[0].toObject(DeportistaDB::class.java)
                                                deportistas.add(deportistaDB!!)
                                            }
                                            DocumentChange.Type.MODIFIED -> {
                                                val deportistaDB = doc.documents[0].toObject(DeportistaDB::class.java)
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

    override fun onResume() {
        super.onResume()
        cargarClientes()
        loadRecyclerViewAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // First clear current all the menu items
        menu.clear()

        // Add the new menu items
        inflater.inflate(R.menu.main, menu)

        val search = menu?.findItem(R.id.app_bar_search)
        val searchView = search?.actionView as SearchView
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

        if (chats.size > 0){

            binding.rvChats.setHasFixedSize(true)
            binding.rvChats.layoutManager = LinearLayoutManager(requireContext())

            recyclerAdapter.RecyclerAdapter(chats, requireContext())
            binding.rvChats.adapter = recyclerAdapter
            var dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            binding.rvChats.addItemDecoration(dividerItemDecoration)
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
                    chats.clear()
                    setUpRecyclerView()
                    recyclerAdapter.notifyDataSetChanged()
                    for (dc in doc.documentChanges){
                        when (dc.type){
                            DocumentChange.Type.ADDED -> {
                                val chat = dc.document.toObject(Chat::class.java)

                                db.collection("users").whereEqualTo(FieldPath.documentId(),dc.document.id).addSnapshotListener { value, error ->
                                    if (value != null){
                                        if (value.documents.size > 0){
                                            val deportistaDB = value.documents[0].toObject(DeportistaDB::class.java)
                                            chat.deportista = deportistaDB!!
                                            chats.add(chat)
                                            if (_binding!=null){
                                                setUpRecyclerView()
                                                recyclerAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                }

                            }
                            DocumentChange.Type.MODIFIED -> {
                                val chat = dc.document.toObject(Chat::class.java)

                                db.collection("users").whereEqualTo(FieldPath.documentId(),dc.document.id).addSnapshotListener { value, error ->
                                    if (value != null){
                                        val deportistaDB = value.documents[0].toObject(DeportistaDB::class.java)
                                        chat.deportista = deportistaDB!!
                                        for (i in 0 until chats.size){
                                            if (chats[i].deportista.email == chat.deportista.email){
                                                chats[i] = chat
                                            }
                                        }
                                        if (_binding!=null){
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