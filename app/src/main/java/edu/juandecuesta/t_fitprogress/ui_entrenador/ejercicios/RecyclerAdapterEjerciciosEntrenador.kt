package edu.juandecuesta.t_fitprogress.ui_entrenador.ejercicios

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorClienteBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorEjerciciosBinding
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorHomeBinding
import edu.juandecuesta.t_fitprogress.documentFirebase.DeportistaDB
import edu.juandecuesta.t_fitprogress.model.Deportista
import edu.juandecuesta.t_fitprogress.model.Ejercicio
import edu.juandecuesta.t_fitprogress.utils.Functions
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
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
                        .load(R.drawable.ic_icon_play)
                        .centerInside()
                        .into(binding.imageEjerc)
                }
                else -> {
                    Glide.with(context)
                        .load(ejercicio.urlImagen)
                        .centerInside()
                        .into(binding.imageEjerc)
                }
            }
        }

    }

}