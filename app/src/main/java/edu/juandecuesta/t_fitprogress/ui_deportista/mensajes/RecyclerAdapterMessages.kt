package edu.juandecuesta.t_fitprogress.ui_deportista.mensajes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.databinding.RvMensajesBinding
import edu.juandecuesta.t_fitprogress.model.Mensaje
import edu.juandecuesta.t_fitprogress.utils.Functions

class RecyclerAdapterMessages : RecyclerView.Adapter<RecyclerAdapterMessages.ViewHolder>() {

    lateinit var context: Context
    var messages: MutableList<Mensaje> = ArrayList()

    fun RecyclerAdapter(chats: MutableList<Mensaje>, context: Context){
        this.messages = chats

        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvMensajesBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = RvMensajesBinding.bind(view)

        fun bind(message: Mensaje, context: Context) {

            if (message.enviado){
                binding.mensajeRecibido.isVisible = false
                binding.txtMensajeEnviado.text = message.texto

                if (message.fecha != ""){
                    val fecha = message.fecha.split(" ")[0]
                    val hora = message.fecha.split(" ")[1]
                    if (Functions().calcularFecha(fecha) == 0){
                        binding.txtHoraEnviada.text = hora
                    } else{
                        val dia = message.fecha.split("/")[0]
                        val mes = message.fecha.split("/")[1].toInt()
                        val array = context.resources.getStringArray(R.array.mesesAbrev)
                        val fecha = "${dia} ${array[mes-1]}"
                        binding.txtHoraEnviada.text = fecha
                    }
                }
            } else {
                binding.mensajeEnviado.isVisible = false
                binding.txtMensajeRecibido.text = message.texto

                if (message.fecha != ""){
                    val fecha = message.fecha.split(" ")[0]
                    val hora = message.fecha.split(" ")[1]
                    if (Functions().calcularFecha(fecha) == 0){
                        binding.txtHoraRecibido.text = hora
                    } else{
                        val dia = message.fecha.split("/")[0]
                        val mes = message.fecha.split("/")[1].toInt()
                        val array = context.resources.getStringArray(R.array.mesesAbrev)
                        val fecha = "${dia} ${array[mes-1]}"
                        binding.txtHoraRecibido.text = fecha
                    }
                }
            }

        }

    }

}