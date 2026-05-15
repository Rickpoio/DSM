package com.example.proyectodecatedra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectodecatedra.data.MovimientoResponse
import com.example.proyectodecatedra.network.SupabaseProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashBoardFragment : Fragment() {

    private lateinit var tvSaldoDisponible: TextView
    private lateinit var tvMesActual: TextView
    private lateinit var tvIngresos: TextView
    private lateinit var tvGastos: TextView
    private lateinit var tvPresupuestoGeneral: TextView
    private lateinit var btnGenerarReporte: Button
    
    // Elementos de la alerta dinámica
    private lateinit var llAlertStatus: LinearLayout
    private lateinit var ivAlertIcon: ImageView
    private lateinit var tvAlertText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dash_board, container, false)

        tvSaldoDisponible = view.findViewById(R.id.tvSaldoDisponible)
        tvMesActual = view.findViewById(R.id.tvMesActual)
        tvIngresos = view.findViewById(R.id.tvIngresos)
        tvGastos = view.findViewById(R.id.tvGastos)
        tvPresupuestoGeneral = view.findViewById(R.id.tvPresupuestoGeneral)
        btnGenerarReporte = view.findViewById(R.id.btnGenerarReporte)
        
        llAlertStatus = view.findViewById(R.id.llAlertStatus)
        ivAlertIcon = view.findViewById(R.id.ivAlertIcon)
        tvAlertText = view.findViewById(R.id.tvAlertText)

        btnGenerarReporte.setOnClickListener {
            val fragment = ReporteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        setupCurrentMonth()
        obtenerDatosDashboard()

        return view
    }

    private fun setupCurrentMonth() {
        val monthFormat = SimpleDateFormat("MMMM", Locale("es", "ES"))
        val currentMonth = monthFormat.format(Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        tvMesActual.text = currentMonth
    }

    private fun obtenerDatosDashboard() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = SupabaseProvider.client
                    .from("movimientos")
                    .select()
                    .decodeList<MovimientoResponse>()

                val movimientosUsuario = response.filter { it.usuario_id == usuarioId }

                // Obtener mes y año actual para validar movimientos
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH) // 0-11
                val currentYear = calendar.get(Calendar.YEAR)

                var ingresosMes = 0.0
                var gastosMes = 0.0

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                movimientosUsuario.forEach { mov ->
                    val fechaStr = mov.fecha ?: ""
                    if (fechaStr.isNotEmpty()) {
                        try {
                            val fechaMov = sdf.parse(fechaStr)
                            val calMov = Calendar.getInstance()
                            calMov.time = fechaMov

                            // Validación: Solo procesar si el mes y año coinciden con el actual
                            if (calMov.get(Calendar.MONTH) == currentMonth && 
                                calMov.get(Calendar.YEAR) == currentYear) {
                                
                                if (mov.tipo?.lowercase() == "ingreso") {
                                    ingresosMes += (mov.monto ?: 0.0)
                                } else if (mov.tipo?.lowercase() == "gasto") {
                                    gastosMes += (mov.monto ?: 0.0)
                                }
                            }
                        } catch (e: Exception) {
                            // Error al parsear fecha, se ignora el movimiento
                        }
                    }
                }

                // El Saldo Disponible ahora solo refleja el balance del mes actual (Ingresos - Gastos)
                val saldoDelMes = ingresosMes - gastosMes

                // Actualizar UI con datos filtrados
                tvSaldoDisponible.text = String.format("$%.2f USD", saldoDelMes)
                tvIngresos.text = String.format("$%.2f USD", ingresosMes)
                tvGastos.text = String.format("$%.2f USD", gastosMes)
                tvPresupuestoGeneral.text = String.format("$%.2f USD", ingresosMes)
                
                // Actualizar Estado del Saldo basado solo en el balance mensual
                actualizarEstadoSaldo(saldoDelMes)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarEstadoSaldo(saldo: Double) {
        if (!isAdded) return
        when {
            saldo >= 75.0 -> {
                llAlertStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_alert_green)
                ivAlertIcon.setImageResource(android.R.drawable.ic_dialog_info)
                ivAlertIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_icon))
                tvAlertText.text = "Saldo en buen estado"
                tvAlertText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_icon))
            }
            saldo >= 30.0 -> {
                llAlertStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_alert_yellow)
                ivAlertIcon.setImageResource(android.R.drawable.ic_dialog_alert)
                ivAlertIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow_icon))
                tvAlertText.text = "Saldo en estado aceptable"
                tvAlertText.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_icon))
            }
            else -> {
                llAlertStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_alert_red)
                ivAlertIcon.setImageResource(android.R.drawable.ic_dialog_alert)
                ivAlertIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red_icon))
                tvAlertText.text = "Saldo bajo"
                tvAlertText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_icon))
            }
        }
    }
}