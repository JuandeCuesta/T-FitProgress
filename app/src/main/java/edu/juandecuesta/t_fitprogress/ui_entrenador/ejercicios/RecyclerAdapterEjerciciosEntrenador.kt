package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorEjerciciosBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import kotlin.collections.ArrayList

class RecyclerAdapterEjerciciosEntrenador: RecyclerView.Adapter<RecyclerAdapterEjerciciosEntrenador.ViewHolder>() {

    lateinit var context: Context
    var ejercicios: MutableList<Ejercicio> = ArrayList()
    var copy: MutableList<Ejercicio> = ArrayList()

    fun RecyclerAdapter(deportistas: MutableList<Ejercicio> ,context: Context){
        this.ejercicios = deportistas

        this.context = context
    }

    fun filter (text:String){

        if (this.copy.size == 0){
            this.copy.addAll(ejercicios)
        }
        ejercicios.clear()
        if (text.isEmpty()) {
            ejercicios.addAll(this.copy)
        } else {
            for (item in this.copy) {
                if (item.nombre.lowercase().contains(text.lowercase()) || item.tipo.lowercase()
                        .contains(text.lowercase()) || item.grupoMuscular.lowercase()
                        .contains(text.lowercase())
                ) {
                    ejercicios.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvEntrenadorEjerciciosBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ejercicios.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return ejercicios.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = RvEntrenadorEjerciciosBinding.bind(view)

        fun bind(ejercicio: Ejercicio, context: Context) {
            binding.tvNombreEj.text = ejercicio.nombre
            binding.tvMuscularGroup.text = ejercicio.grupoMuscular
            binding.tvTipoEj.text = ejercicio.tipo

            when {
                ejercicio.urlImagen != "" -> {
                    Glide.with(context)
                        .load(ejercicio.urlImagen)
                        .centerInside()
                        .into(binding.imageEjerc)
                }
                ejercicio.urlVideo != "" -> {
                    Glide.with(context)
                        .load(R.drawable.icon_video)
                        .centerInside()
                        .into(binding.imageEjerc)
                }
                else -> {
                    Glide.with(context)
                        .load(R.drawable.icon_noimagen)
                        .centerInside()
                        .into(binding.imageEjerc)
                }
            }

            itemView.setOnClickListener {

                val ejercIntent = Intent (context, EditEjercicioActivity::class.java).apply {
                    putExtra("ejercicio", ejercicio)
                }
                ContextCompat.startActivity(context,ejercIntent, Bundle.EMPTY)

            }
        }

    }

}