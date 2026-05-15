package com.example.proyectodecatedra

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodecatedra.adapter.MovimientoAdapter
import com.example.proyectodecatedra.data.MovimientoResponse
import com.example.proyectodecatedra.network.SupabaseProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MisMovimientosFragment : Fragment() {

    private lateinit var rvMovimientos: RecyclerView

    private lateinit var adapter: MovimientoAdapter

    private lateinit var etFecha: TextInputEditText

    private lateinit var spTipo: Spinner
    private lateinit var spMetodoPago: Spinner

    private lateinit var btnMonto: Button

    private var montoMinimo = 0.0
    private var montoMaximo = 1000.0

    private var listaMovimientos =
        mutableListOf<MovimientoResponse>()

    private var listaOriginal =
        mutableListOf<MovimientoResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_mis_movimientos,
            container,
            false
        )

        rvMovimientos =
            view.findViewById(R.id.rvMovimientos)

        etFecha =
            view.findViewById(R.id.etFecha)

        spTipo =
            view.findViewById(R.id.spTipo)

        spMetodoPago =
            view.findViewById(R.id.spMetodoPago)

        btnMonto =
            view.findViewById(R.id.btnMonto)

        rvMovimientos.layoutManager =
            LinearLayoutManager(requireContext())

        adapter = MovimientoAdapter(
            listaMovimientos,

            onEditar = { movimiento ->
                editarMovimiento(movimiento)
            },

            onEliminar = { movimiento ->
                eliminarMovimiento(movimiento)
            }
        )

        rvMovimientos.adapter = adapter

        configurarDatePicker()

        configurarSpinnerTipo()

        configurarSpinnerMetodoPago()

        configurarFiltroMonto()

        obtenerMovimientos()

        return view
    }

    private fun configurarDatePicker() {

        etFecha.setOnClickListener {

            mostrarDatePicker()
        }
    }

    private fun mostrarDatePicker() {

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Selecciona una fecha")
            .build()

        datePicker.show(
            parentFragmentManager,
            "DATE_PICKER"
        )

        datePicker.addOnPositiveButtonClickListener { selection ->

            val formato = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

            formato.timeZone =
                TimeZone.getTimeZone("UTC")

            val fecha =
                formato.format(selection)

            etFecha.setText(fecha)

            filtrarMovimientos()
        }
    }

    private fun configurarSpinnerTipo() {

        val opciones = listOf(
            "Tipo",
            "Gasto",
            "Ingreso"
        )

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            opciones
        )

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spTipo.adapter = adapterSpinner

        spTipo.onItemSelectedListener =
            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    filtrarMovimientos()
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
    }

    private fun configurarSpinnerMetodoPago() {

        val opciones = listOf(
            "Método",
            "Efectivo",
            "Tarjeta"

        )

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            opciones
        )

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spMetodoPago.adapter = adapterSpinner

        spMetodoPago.onItemSelectedListener =
            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    filtrarMovimientos()
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
    }

    private fun configurarFiltroMonto() {

        btnMonto.setOnClickListener {

            mostrarDialogoMonto()
        }
    }

    private fun mostrarDialogoMonto() {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_filtro_monto,
            null
        )

        val seekMin =
            dialogView.findViewById<SeekBar>(R.id.seekMin)

        val seekMax =
            dialogView.findViewById<SeekBar>(R.id.seekMax)

        val tvMin =
            dialogView.findViewById<TextView>(R.id.tvMin)

        val tvMax =
            dialogView.findViewById<TextView>(R.id.tvMax)

        seekMin.progress = montoMinimo.toInt()
        seekMax.progress = montoMaximo.toInt()

        tvMin.text = "$${montoMinimo}"
        tvMax.text = "$${montoMaximo}"

        seekMin.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    montoMinimo = progress.toDouble()

                    tvMin.text = "$$progress"
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }
            }
        )

        seekMax.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    montoMaximo = progress.toDouble()

                    tvMax.text = "$$progress"
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }
            }
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Aplicar") { _, _ ->

                filtrarMovimientos()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun filtrarMovimientos() {

        val fechaFiltro =
            etFecha.text.toString().trim()

        val metodoPagoFiltro =
            spMetodoPago.selectedItem.toString()

        val tipoFiltro =
            spTipo.selectedItem.toString()

        val filtrados = listaOriginal.filter { movimiento ->

            val monto =
                movimiento.monto ?: 0.0

            val coincideFecha =
                fechaFiltro.isEmpty() ||
                        movimiento.fecha?.contains(
                            fechaFiltro,
                            true
                        ) == true

            val coincideMetodoPago =
                metodoPagoFiltro == "Método" ||
                        movimiento.metodo_pago.equals(
                            metodoPagoFiltro,
                            true
                        )

            val coincideTipo =
                tipoFiltro == "Tipo" ||
                        movimiento.tipo.equals(
                            tipoFiltro,
                            true
                        )

            val coincideMonto =
                monto in montoMinimo..montoMaximo

            coincideFecha &&
                    coincideMetodoPago &&
                    coincideTipo &&
                    coincideMonto
        }

        listaMovimientos.clear()

        listaMovimientos.addAll(filtrados)

        adapter.notifyDataSetChanged()
    }

    private fun obtenerMovimientos() {

        val usuarioId = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid ?: ""

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = SupabaseProvider.client
                    .from("movimientos")
                    .select()
                    .decodeList<MovimientoResponse>()

                listaOriginal.clear()

                listaOriginal.addAll(
                    response.filter {
                        it.usuario_id == usuarioId
                    }
                )

                listaMovimientos.clear()

                listaMovimientos.addAll(
                    listaOriginal
                )

                adapter.notifyDataSetChanged()

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun eliminarMovimiento(
        movimiento: MovimientoResponse
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                SupabaseProvider.client
                    .from("movimientos")
                    .delete {

                        filter {

                            filter(
                                column = "id",
                                operator = FilterOperator.EQ,
                                value = movimiento.id
                            )
                        }
                    }

                Toast.makeText(
                    requireContext(),
                    "Movimiento eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                obtenerMovimientos()

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun editarMovimiento(
        movimiento: MovimientoResponse
    ) {

        val fragment = MovimientosFragment()

        val bundle = Bundle()

        bundle.putInt("id", movimiento.id)

        bundle.putString(
            "descripcion",
            movimiento.descripcion
        )

        bundle.putString(
            "metodo_pago",
            movimiento.metodo_pago
        )

        bundle.putDouble(
            "monto",
            movimiento.monto ?: 0.0
        )

        bundle.putString(
            "fecha",
            movimiento.fecha
        )

        bundle.putString(
            "tipo",
            movimiento.tipo
        )

        fragment.arguments = bundle

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }
}