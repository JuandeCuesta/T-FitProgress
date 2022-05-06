package edu.juandecuesta.t_fitprogress.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenamientosBinding
import edu.juandecuesta.t_fitprogress.databinding.RvSelectEntrenBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos.EditEntrenamientoActivity

class RecyclerAdapterSelectEntrenamiento: RecyclerView.Adapter<RecyclerAdapterSelectEntrenamiento.ViewHolder>() {
    lateinit var context: Context
    var entrenamientos: MutableList<Entrenamiento> = ArrayList()
    var copy: MutableList<Entrenamiento> = ArrayList()
    var itemSelected: Entrenamiento = Entrenamiento()


    fun RecyclerAdapter(entrenamientos: MutableList<Entrenamiento>, context: Context){
        this.entrenamientos = entrenamientos
        this.context = context
    }

    fun getItem (): Entrenamiento {
        return this.itemSelected
    }

    fun filter (text:String){

        if (this.copy.size == 0){
            this.copy.addAll(entrenamientos)
        }
        entrenamientos.clear()
        if (text.isEmpty()) {
            entrenamientos.addAll(this.copy)
        } else {
            for (item in this.copy) {
                if (item.nombre.lowercase().contains(text.lowercase()) || item.tipo.lowercase()
                        .contains(text.lowercase())
                ) {
                    entrenamientos.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvSelectEntrenBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = entrenamientos.get(position)
        holder.bind(item,context)
        holder.itemView.setBackgroundColor(if (itemSelected.id == item.id) Color.GREEN else Color.TRANSPARENT)
        holder.itemView.setOnClickListener {
            itemSelected = item
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return entrenamientos.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = RvSelectEntrenBinding.bind(view)

        fun bind(entrenamiento: Entrenamiento, context: Context) {
            binding.tvNombreEnt.text = entrenamiento.nombre
            binding.tvtipoEnt.text = entrenamiento.tipo
            binding.idEntreno.text = entrenamiento.id

            if (entrenamiento.ejercicios.size > 1){
                binding.tvseriesrep.text = ("${entrenamiento.ejercicios.size} ejercicios - ${entrenamiento.series}x${entrenamiento.repeticiones}RM")
            } else binding.tvseriesrep.text = ("${entrenamiento.ejercicios.size} ejercicio - ${entrenamiento.series}x${entrenamiento.repeticiones}RM")
        }

    }


}