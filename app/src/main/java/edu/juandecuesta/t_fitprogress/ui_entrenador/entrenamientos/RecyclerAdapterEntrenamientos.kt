package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorEjerciciosBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenamientosBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.model.Entrenamiento
import edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios.EditEjercicioActivity

class RecyclerAdapterEntrenamientos:RecyclerView.Adapter<RecyclerAdapterEntrenamientos.ViewHolder>() {

    lateinit var context: Context
    var entrenamientos: MutableList<Entrenamiento> = ArrayList()
    var copy: MutableList<Entrenamiento> = ArrayList()


    fun RecyclerAdapter(entrenamientos: MutableList<Entrenamiento> ,context: Context){
        this.entrenamientos = entrenamientos

        this.context = context
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
        return ViewHolder(RvEntrenamientosBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = entrenamientos.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return entrenamientos.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = RvEntrenamientosBinding.bind(view)

        fun bind(entrenamiento: Entrenamiento, context: Context) {
            binding.tvNombreEnt.text = entrenamiento.nombre
            binding.tvtipoEnt.text = ("Entrenamiento de ${entrenamiento.tipo}")

            if (entrenamiento.ejercicios.size > 1){
                binding.tvseriesrep.text = ("${entrenamiento.ejercicios.size} ejercicios - ${entrenamiento.series}x${entrenamiento.repeticiones}RM")
            } else binding.tvseriesrep.text = ("${entrenamiento.ejercicios.size} ejercicio - ${entrenamiento.series}x${entrenamiento.repeticiones}RM")


            when (entrenamiento.tipo) {
                "Resistencia" -> {
                    Glide.with(context)
                        .load(R.drawable.resistencia)
                        .centerInside()
                        .into(binding.imageEntren)
                }
                "Fuerza" -> {
                    Glide.with(context)
                        .load(R.drawable.potencia)
                        .centerInside()
                        .into(binding.imageEntren)
                }
                "Flexibilidad" -> {
                    Glide.with(context)
                        .load(R.drawable.flexibilidad)
                        .centerInside()
                        .into(binding.imageEntren)
                }
                "Velocidad" -> {
                    Glide.with(context)
                        .load(R.drawable.velocidad)
                        .centerInside()
                        .into(binding.imageEntren)
                }

                else -> {
                    Glide.with(context)
                        .load(R.drawable.prueba)
                        .centerInside()
                        .into(binding.imageEntren)
                }
            }

            itemView.setOnClickListener {

                val ejercIntent = Intent (context, EditEntrenamientoActivity::class.java).apply {
                    putExtra("entrenamiento", entrenamiento)
                }
                ContextCompat.startActivity(context,ejercIntent, Bundle.EMPTY)

            }
        }

    }
}