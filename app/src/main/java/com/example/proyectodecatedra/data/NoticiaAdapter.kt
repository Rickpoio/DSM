package com.example.proyectodecatedra.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodecatedra.R


class NoticiaAdapter (private val noticias: List<Noticia>)
    : RecyclerView.Adapter<NoticiaAdapter.ViewHolder>(){
        private var onItemClick : OnItemClickListener?=null
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tituloTextView: TextView = view.findViewById(R.id.tituloNoticia)
        val categoriaTextView: TextView = view.findViewById(R.id.categoriaNoticia)
        val fechaTextView: TextView = view.findViewById(R.id.fechaNoticia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder,position:Int) {
        val noticia=noticias[position]
        holder.tituloTextView.text=noticia.titulo
        holder.categoriaTextView.text=noticia.categoria
        holder.fechaTextView.text=noticia.fecha

       holder.itemView.setOnClickListener {
           onItemClick?.onItemClick(noticia)
       }
    }

    override fun getItemCount(): Int {

        return noticias.size
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        onItemClick=listener

    }
    interface OnItemClickListener{
        fun onItemClick(noticia: Noticia)
    }

}