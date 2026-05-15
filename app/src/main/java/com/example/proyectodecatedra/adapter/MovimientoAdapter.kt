package com.example.proyectodecatedra.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodecatedra.R
import com.example.proyectodecatedra.data.MovimientoResponse
import com.google.android.material.chip.Chip

class MovimientoAdapter(
    private val lista: MutableList<MovimientoResponse>,
    private val onEditar: (MovimientoResponse) -> Unit,
    private val onEliminar: (MovimientoResponse) -> Unit
) : RecyclerView.Adapter<MovimientoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvHora: TextView = view.findViewById(R.id.tvHora)


        val chipMetodo: Chip = view.findViewById(R.id.chipMetodo)
        val chipCategoria: Chip = view.findViewById(R.id.chipCategoria)

        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val movimiento = lista[position]

        val fechaCompleta = movimiento.fecha_creacion ?: ""

        if (fechaCompleta.contains("T")) {

            val hora = fechaCompleta
                .substringAfter("T")
                .substring(0,5)

            holder.tvHora.text = hora

        } else {

            holder.tvHora.text = ""
        }

        holder.tvTitulo.text =
            movimiento.descripcion ?: "Sin descripción"

        holder.tvMonto.text =
            "$${movimiento.monto}"

        holder.tvFecha.text =
            movimiento.fecha ?: ""

        holder.chipMetodo.text =
            movimiento.metodo_pago ?: ""

        holder.chipCategoria.text =
            movimiento.tipo ?: ""

        holder.btnEditar.setOnClickListener {
            onEditar(movimiento)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(movimiento)
        }
    }

    fun actualizarLista(nuevaLista: List<MovimientoResponse>) {

        lista.clear()
        lista.addAll(nuevaLista)

        notifyDataSetChanged()
    }
}