package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.ListviewEjerciciosBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorClienteBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorEjerciciosBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorHomeBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.ui_entrenador.MainActivity
import edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios.EditEjercicioActivity
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
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

                val ejercIntent = Intent (context, EditEjercicioActivity::class.java).apply {
                    putExtra("ejercicio", ejercicio)
                }
                ContextCompat.startActivity(context,ejercIntent, Bundle.EMPTY)
            }
        }

    }

}