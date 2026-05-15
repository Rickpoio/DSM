package com.example.proyectodecatedra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectodecatedra.data.MovimientoResponse
import com.example.proyectodecatedra.network.SupabaseProvider
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReporteFragment : Fragment() {

    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvBalanceFinal: TextView
    private lateinit var btnBack: ImageButton
    
    private lateinit var cardIngresos: MaterialCardView
    private lateinit var llDetalleIngresos: LinearLayout
    private lateinit var cardGastos: MaterialCardView
    private lateinit var llDetalleGastos: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reporte, container, false)

        tvTotalIngresos = view.findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = view.findViewById(R.id.tvTotalGastos)
        tvBalanceFinal = view.findViewById(R.id.tvBalanceFinal)
        btnBack = view.findViewById(R.id.btnBack)
        
        cardIngresos = view.findViewById(R.id.cardIngresos)
        llDetalleIngresos = view.findViewById(R.id.llDetalleIngresos)
        cardGastos = view.findViewById(R.id.cardGastos)
        llDetalleGastos = view.findViewById(R.id.llDetalleGastos)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        cardIngresos.setOnClickListener {
            toggleDetalle(llDetalleIngresos)
        }

        cardGastos.setOnClickListener {
            toggleDetalle(llDetalleGastos)
        }

        obtenerDatosReporte()

        return view
    }

    private fun toggleDetalle(container: LinearLayout) {
        if (container.visibility == View.VISIBLE) {
            container.visibility = View.GONE
        } else {
            container.visibility = View.VISIBLE
        }
    }

    private fun obtenerDatosReporte() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = SupabaseProvider.client
                    .from("movimientos")
                    .select()
                    .decodeList<MovimientoResponse>()

                val movimientosUsuario = response.filter { it.usuario_id == usuarioId }

                // Filtro por mes actual
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                var totalIngresos = 0.0
                var totalGastos = 0.0

                llDetalleIngresos.removeAllViews()
                llDetalleGastos.removeAllViews()

                movimientosUsuario.forEach { mov ->
                    val fechaStr = mov.fecha ?: ""
                    if (fechaStr.isNotEmpty()) {
                        try {
                            val fechaMov = sdf.parse(fechaStr)
                            val calMov = Calendar.getInstance()
                            calMov.time = fechaMov

                            if (calMov.get(Calendar.MONTH) == currentMonth && 
                                calMov.get(Calendar.YEAR) == currentYear) {
                                
                                val detalleView = layoutInflater.inflate(R.layout.item_reporte_detalle, null)
                                detalleView.findViewById<TextView>(R.id.tvDetalleNombre).text = mov.descripcion ?: "Sin nombre"
                                detalleView.findViewById<TextView>(R.id.tvDetalleFecha).text = mov.fecha
                                detalleView.findViewById<TextView>(R.id.tvDetalleMonto).text = String.format("$%.2f", mov.monto ?: 0.0)

                                if (mov.tipo?.lowercase() == "ingreso") {
                                    totalIngresos += (mov.monto ?: 0.0)
                                    llDetalleIngresos.addView(detalleView)
                                } else if (mov.tipo?.lowercase() == "gasto") {
                                    totalGastos += (mov.monto ?: 0.0)
                                    llDetalleGastos.addView(detalleView)
                                }
                            }
                        } catch (e: Exception) {}
                    }
                }

                val balanceFinal = totalIngresos - totalGastos

                tvTotalIngresos.text = String.format("$%.2f", totalIngresos)
                tvTotalGastos.text = String.format("$%.2f", totalGastos)
                tvBalanceFinal.text = String.format("$%.2f", balanceFinal)

            } catch (e: Exception) {
                e.printStackTrace()
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error al cargar reporte: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
