package edu.juandecuesta.t_fitprogress.ui_deportista.perfil.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvHistorialEntrenamientosBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos.EditEntrenamientoActivity

class RecyclerAdapterHistorial: RecyclerView.Adapter<RecyclerAdapterHistorial.ViewHolder>() {

    lateinit var context: Context
    var entrenamientos: MutableList<Entrenamiento_Deportista> = ArrayList()


    fun RecyclerAdapter(entrenamientos: MutableList<Entrenamiento_Deportista>, context: Context){
        this.entrenamientos = entrenamientos
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvHistorialEntrenamientosBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = entrenamientos.get(position)
        if (position > 0){
            val itemPrevious = entrenamientos[position - 1]
            if (item.primerodeldia || item.fechaFormat != itemPrevious.fechaFormat) item.primerodeldia = true
        } else item.primerodeldia = true


        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return entrenamientos.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = RvHistorialEntrenamientosBinding.bind(view)

        fun bind(entrenamientoDeportista: Entrenamiento_Deportista, context: Context) {


            binding.txtFecha.text = entrenamientoDeportista.fecha

            binding.txtFecha.isVisible = entrenamientoDeportista.primerodeldia

            binding.tvNombreEnt.text = entrenamientoDeportista.entrenamiento!!.nombre
            binding.tvtipoEnt.text = ("Entrenamiento de ${entrenamientoDeportista.entrenamiento!!.tipo}")

            if (entrenamientoDeportista.entrenamiento!!.ejercicios.size > 1){
                binding.tvseriesrep.text = ("${entrenamientoDeportista.entrenamiento!!.ejercicios.size} ejercicios - ${entrenamientoDeportista.entrenamiento!!.series}x${entrenamientoDeportista.entrenamiento!!.repeticiones}RM")
            } else binding.tvseriesrep.text = ("${entrenamientoDeportista.entrenamiento!!.ejercicios.size} ejercicio - ${entrenamientoDeportista.entrenamiento!!.series}x${entrenamientoDeportista.entrenamiento!!.repeticiones}RM")


            when (entrenamientoDeportista.entrenamiento!!.tipo) {
                "Resistencia" -> {
                    Glide.with(context)
                        .load(R.drawable.resistencia)
                        .centerInside()
                        .into(binding.imageEntren)
                }
                "Potencia" -> {
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
                "Hipertrofia" -> {
                    Glide.with(context)
                        .load(R.drawable.hipertrofia)
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
            when (entrenamientoDeportista.realizado) {
                true -> {
                    Glide.with(context)
                        .load(R.drawable.ic_baseline_check_circle_24)
                        .centerInside()
                        .into(binding.imagenRealizado)
                }
                false -> {
                    Glide.with(context)
                        .load(R.drawable.ic_baseline_cancel_24)
                        .centerInside()
                        .into(binding.imagenRealizado)
                }
            }

            itemView.setOnClickListener {
                val entrenIntent = Intent (context, EditEntrenamientoActivity::class.java).apply {
                    putExtra("entrenamiento", entrenamientoDeportista.entrenamiento)
                }
                ContextCompat.startActivity(context,entrenIntent, Bundle.EMPTY)
            }
        }

    }
}