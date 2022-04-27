package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorClienteBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorHomeBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Deportista
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapterClientesEntrenador: RecyclerView.Adapter<RecyclerAdapterClientesEntrenador.ViewHolder>() {

    lateinit var context: Context
    var deportistas: MutableList<DeportistaDB> = ArrayList()
    var copy: MutableList<DeportistaDB> = ArrayList()

    fun RecyclerAdapter(deportistas: MutableList<DeportistaDB> ,context: Context){
        this.deportistas = deportistas

        this.context = context
    }

    fun filter (text:String){

        if (this.copy.size == 0){
            this.copy.addAll(deportistas)
        }
        deportistas.clear()
        if (text.isEmpty()) {
            deportistas.addAll(this.copy)
        } else {
            for (item in this.copy) {
                if (item.nombre.lowercase().contains(text.lowercase()) || item.apellido.lowercase()
                        .contains(text.lowercase())
                ) {
                    deportistas.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvEntrenadorClienteBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = deportistas.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return deportistas.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = RvEntrenadorClienteBinding.bind(view)

        fun bind(deportista: DeportistaDB, context: Context) {
            val nombreCompleto = (deportista.nombre + " " + deportista.apellido)
            binding.tvNombre.text = nombreCompleto
            binding.tvFecha.text = deportista.fechanacimiento
        }

    }

}