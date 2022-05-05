package edu.juandecuesta.t_fitprogress.ui_entrenador.entrenamientos

import android.widget.BaseAdapter
import android.content.Context

import android.widget.TextView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import edu.juandecuesta.t_fitprogress.R
import edu.juandecuesta.t_fitprogress.model.Ejercicio


class addEjercAdapter: BaseAdapter() {


    lateinit var context: Context
    var ejercicios: MutableList<Ejercicio> = ArrayList()

    fun adapterList(context: Context, items: MutableList<Ejercicio>) {
        this.context = context
        this.ejercicios = items
    }

    fun getList():MutableList<Ejercicio>{
        return ejercicios
    }

    override fun getCount(): Int {
        return this.ejercicios.size
    }

    override fun getItem(position: Int): Any? {
        return this.ejercicios.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        val v: View
        if (view == null) {
            val inflater = LayoutInflater.from(this.context)
            v = inflater.inflate(R.layout.listview_checkitem, null)
        } else v = view
        //Obtenemos el nombre de la lista según la posición que nos pasan.
        val ejerc = ejercicios.get(position)

        val tvNombre: TextView = v.findViewById(R.id.X_item_text)
        val btn: ImageButton = v.findViewById(R.id.btnDeleteEjerc)
        //El imageView podríamos buscar una imagen de la persona y colocarlo en vez de la imagen por defecto
        //Pero en este ejemplo eso no está implementado.
        //En el textView si ponemos el nombre.
        tvNombre.text = ejerc.nombre

        btn.setOnClickListener {
            ejercicios.remove(ejerc)
            notifyDataSetChanged()
        }

        //Devolvemos la vista (nuestro layout) actualizada para la posición requerida.
        return v
    }

}