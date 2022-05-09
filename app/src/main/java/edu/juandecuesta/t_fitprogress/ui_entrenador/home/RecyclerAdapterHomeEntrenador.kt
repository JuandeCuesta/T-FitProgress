package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorHomeBinding
import edu.juandecuesta.t_fitprogress.model.Deportista
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapterHomeEntrenador: RecyclerView.Adapter<RecyclerAdapterHomeEntrenador.ViewHolder>() {

    lateinit var context: Context
    var deportistas: MutableList<Deportista> = ArrayList()

    fun RecyclerAdapter(deportistas: MutableList<Deportista> ,context: Context){
        this.deportistas = deportistas
        this.context = context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvEntrenadorHomeBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = deportistas.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return deportistas.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = RvEntrenadorHomeBinding.bind(view)

        fun bind(deportista: Deportista, context: Context) {
            binding.tvNombre.text = deportista.nombre

            //val entreno = deportista.entrenamientos?.find { e->e.fecha == Date() }
            //binding.tvTipoEntrenamiento.text = entreno?.entrenamiento?.tipo
        }

    }

}