package edu.juandecuesta.t_fitprogress.ui_deportista.entrenamientos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.juandecuesta.t_fitprogress.databinding.ListviewEjerciciosBinding
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios.EditEjercicioActivity
import kotlin.collections.ArrayList

class RecyclerAdapterEjerciciosEntrenamientos: RecyclerView.Adapter<RecyclerAdapterEjerciciosEntrenamientos.ViewHolder>() {

    lateinit var context: Context
    var ejercicios: MutableList<Ejercicio> = ArrayList()

    fun RecyclerAdapter(ejercicios: MutableList<Ejercicio> ,context: Context){
        this.ejercicios = ejercicios

        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListviewEjerciciosBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ejercicios.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return ejercicios.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = ListviewEjerciciosBinding.bind(view)

        fun bind(ejercicio: Ejercicio, context: Context) {
            binding.nombreEjerc.text = ejercicio.nombre

            itemView.setOnClickListener {

                val ejercIntent = Intent (context, ShowEjercicioActivity::class.java).apply {
                    putExtra("ejercicio", ejercicio)
                }
                ContextCompat.startActivity(context,ejercIntent, Bundle.EMPTY)
            }
        }

    }

}