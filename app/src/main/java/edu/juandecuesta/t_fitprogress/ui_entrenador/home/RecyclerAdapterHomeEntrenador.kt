package edu.juandecuesta.t_fitprogress.ui_entrenador.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorHomeBinding
import edu.juandecuesta.t_fitprogress.model.Entrenamiento_Deportista
import kotlin.collections.ArrayList

class RecyclerAdapterHomeEntrenador: RecyclerView.Adapter<RecyclerAdapterHomeEntrenador.ViewHolder>() {

    lateinit var context: Context
    var entrenamientos: MutableList<Entrenamiento_Deportista> = ArrayList()
    var copy: MutableList<Entrenamiento_Deportista> = ArrayList()

    fun RecyclerAdapter(entrenamientos: MutableList<Entrenamiento_Deportista> ,context: Context){
        this.entrenamientos = entrenamientos
        this.context = context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvEntrenadorHomeBinding.inflate(layoutInflater,parent,false).root)
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
                if (item.deportista.nombre.lowercase().contains(text.lowercase()) || item.deportista.apellido.lowercase().contains(text.lowercase())
                    || item.entrenamiento?.nombre?.lowercase()!!.contains(text.lowercase())
                    || item.entrenamiento?.tipo?.lowercase()!!.contains(text.lowercase())
                ) {
                    entrenamientos.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = entrenamientos.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return entrenamientos.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = RvEntrenadorHomeBinding.bind(view)

        fun bind(entrenamientoDeportista: Entrenamiento_Deportista, context: Context) {

            binding.tvNombreDep.text = ("${entrenamientoDeportista.deportista!!.nombre} ${entrenamientoDeportista.deportista!!.apellido}")
            binding.tvNombreEntre.text = ("${entrenamientoDeportista.entrenamiento!!.nombre} - ${entrenamientoDeportista.entrenamiento!!.tipo}")

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

            /*itemView.setOnClickListener {
                val entrenIntent = Intent (context, EditEntrenamientoActivity::class.java).apply {
                    putExtra("entrenamiento", entrenamientoDeportista.entrenamiento)
                }
                ContextCompat.startActivity(context,entrenIntent, Bundle.EMPTY)
            }*/
        }

    }

}