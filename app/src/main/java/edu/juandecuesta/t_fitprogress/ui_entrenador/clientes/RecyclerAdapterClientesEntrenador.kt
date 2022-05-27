package edu.juandecuesta.t_fitprogress.ui_entrenador.clientes

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
import edu.juandecuesta.t_fitprogress.databinding.RvEntrenadorClienteBinding
import edu.juandecuesta.t_fitprogress.model.DeportistaDB
import edu.juandecuesta.t_fitprogress.utils.Functions
import kotlin.collections.ArrayList

class RecyclerAdapterClientesEntrenador: RecyclerView.Adapter<RecyclerAdapterClientesEntrenador.ViewHolder>() {

    lateinit var context: Context
    var deportistas: MutableList<DeportistaDB> = ArrayList()
    var copy: MutableList<DeportistaDB> = ArrayList()

    fun RecyclerAdapter(deportistas: MutableList<DeportistaDB>, context: Context){
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

            if (deportista.fechanacimiento != ""){

                deportista.fechanacimiento.replace(" ", "")

                binding.tvFecha.text = context.getString(R.string.edad, (Functions().calcularEdad(deportista.fechanacimiento)))
            }else
            {
                binding.tvFecha.text = ""
            }

            Glide.with(context)
                .load(deportista.urlImagen)
                .error(R.drawable.ic_person_white)
                .into(binding.imageCliente)


            itemView.setOnClickListener {

                val deportIntent = Intent (context, ShowClientActivity::class.java).apply {
                    putExtra("deportista", deportista)
                }
                ContextCompat.startActivity(context,deportIntent, Bundle.EMPTY)

            }
        }

    }

}