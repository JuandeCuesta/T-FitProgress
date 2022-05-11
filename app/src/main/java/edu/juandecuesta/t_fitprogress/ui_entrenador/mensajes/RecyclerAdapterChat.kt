package edu.juandecuesta.t_fitprogress.ui_entrenador.mensajes

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
import edu.juandecuesta.t_fitprogress.databinding.RvChatBinding
import edu.juandecuesta.t_fitprogress.model.Chat
import edu.juandecuesta.t_fitprogress.utils.Functions

class RecyclerAdapterChat : RecyclerView.Adapter<RecyclerAdapterChat.ViewHolder>() {

    lateinit var context: Context
    var chats: MutableList<Chat> = ArrayList()
    var copy: MutableList<Chat> = ArrayList()

    fun RecyclerAdapter(chats: MutableList<Chat>, context: Context){
        this.chats = chats

        this.context = context
    }

    fun filter (text:String){

        if (this.copy.size == 0){
            this.copy.addAll(chats)
        }
        chats.clear()
        if (text.isEmpty()) {
            chats.addAll(this.copy)
        } else {
            for (item in this.copy) {
                if (item.deportista.nombre.lowercase().contains(text.lowercase()) || item.deportista.apellido.lowercase()
                        .contains(text.lowercase())
                ) {
                    chats.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(RvChatBinding.inflate(layoutInflater,parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chats.get(position)
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = RvChatBinding.bind(view)

        fun bind(chat: Chat, context: Context) {
            var nombreCompleto = (chat.deportista.nombre + " " + chat.deportista.apellido)
            if (chat.deportista.apellido.indexOf(" ") > -1){
                nombreCompleto = (chat.deportista.nombre + " " + chat.deportista.apellido.split(" ")[0])
            }

            binding.tvNombreUsuario.text = nombreCompleto

            Glide.with(context)
                .load(chat.deportista.urlImagen)
                .error(R.drawable.ic_person_white)
                .centerInside()
                .into(binding.imageCliente)


            if (chat.mensajes.size > 0){
                val fecha = chat.mensajes.first().fecha.split(" ")[0]
                val dia = fecha.split("/")[0]
                val mes = fecha.split("/")[1].toInt()
                val array = context.resources.getStringArray(R.array.mesesAbrev)
                val fechaFormat =
                    if (Functions().calcularFecha(fecha) == 0) chat.mensajes.first().fecha.split(" ")[1]
                    else "${dia} ${array[mes-1]}"

                binding.tvFechaMensaje.text = fechaFormat
                binding.tvUltimoMensaje.text = chat.mensajes.first().texto
            } else {binding.tvUltimoMensaje.text = ""
                    binding.tvFechaMensaje.text = ""}

            var mensajesSinLeer:Int = 0
            chat.mensajes.forEach { m -> if (!m.leido) mensajesSinLeer++ }
            if (mensajesSinLeer > 0){
                binding.tvMensajesSinLeer.text = mensajesSinLeer.toString()
            } else binding.tvMensajesSinLeer.isVisible = false

            itemView.setOnClickListener {

                val messageIntent = Intent (context, CreateMessageActivity::class.java).apply {
                    putExtra("deportista", chat.deportista)
                }
                ContextCompat.startActivity(context,messageIntent, Bundle.EMPTY)

            }
        }

    }

}