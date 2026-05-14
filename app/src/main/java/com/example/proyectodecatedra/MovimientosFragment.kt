package com.example.proyectodecatedra

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectodecatedra.data.Movimiento
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.lifecycleScope
import com.example.proyectodecatedra.network.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class MovimientosFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var btnIngreso: MaterialButton
    private lateinit var btnGasto: MaterialButton
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etMonto: EditText
    private lateinit var etDia: EditText
    private lateinit var etMes: EditText
    private lateinit var etAnio: EditText

    private lateinit var spCategoria: Spinner
    private lateinit var spMetodoPago: Spinner

    private var tipoMovimiento = "ingreso"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_movimientos,
            container,
            false
        )

        inicializarVistas(view)
        configurarSpinners()
        configurarBotones()

        return view
    }

    private fun inicializarVistas(view: View) {

        btnIngreso = view.findViewById(R.id.btnIngreso)
        btnGasto = view.findViewById(R.id.btnGasto)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCancelar = view.findViewById(R.id.btnCancelar)

        etTitulo = view.findViewById(R.id.etTitulo)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etMonto = view.findViewById(R.id.etMonto)

        etDia = view.findViewById(R.id.etDia)
        etMes = view.findViewById(R.id.etMes)
        etAnio = view.findViewById(R.id.etAnio)

        spCategoria = view.findViewById(R.id.spCategoria)
        spMetodoPago = view.findViewById(R.id.spMetodoPago)
    }

    private fun configurarSpinners() {

        val categorias = listOf(
            "Alimentación",
            "Transporte",
            "Salud",
            "Educación",
            "Entretenimiento",
            "Servicios",
            "Otros"
        )

        val metodosPago = listOf(
            "Efectivo",
            "Tarjeta"
        )

        val adapterCategorias = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )

        val adapterMetodos = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            metodosPago
        )

        spCategoria.adapter = adapterCategorias
        spMetodoPago.adapter = adapterMetodos
    }

    private fun configurarBotones() {

        btnIngreso.setOnClickListener {

            tipoMovimiento = "ingreso"

            btnIngreso.setBackgroundColor(Color.parseColor("#DFF5E1"))
            btnGasto.setBackgroundColor(Color.parseColor("#F3F3F3"))

            Toast.makeText(
                requireContext(),
                "Tipo: Ingreso",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnGasto.setOnClickListener {

            tipoMovimiento = "gasto"

            btnGasto.setBackgroundColor(Color.parseColor("#FFDADA"))
            btnIngreso.setBackgroundColor(Color.parseColor("#F3F3F3"))

            Toast.makeText(
                requireContext(),
                "Tipo: Gasto",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnGuardar.setOnClickListener {

            guardarMovimiento()
        }

        btnCancelar.setOnClickListener {

            limpiarFormulario()

            Toast.makeText(
                requireContext(),
                "Formulario limpiado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun guardarMovimiento() {

        val titulo = etTitulo.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val montoTexto = etMonto.text.toString().trim()

        val dia = etDia.text.toString().trim()
        val mes = etMes.text.toString().trim()
        val anio = etAnio.text.toString().trim()

        val metodoPago = spMetodoPago.selectedItem.toString()

        if (titulo.isEmpty()) {

            etTitulo.error = "Ingrese un título"
            return
        }

        if (montoTexto.isEmpty()) {

            etMonto.error = "Ingrese un monto"
            return
        }

        if (dia.isEmpty() || mes.isEmpty() || anio.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Complete la fecha",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val fecha = "$anio-$mes-$dia"

        // TEMPORAL
        val categoriaSeleccionada = spCategoria.selectedItem.toString()

        val categoriaId = when (categoriaSeleccionada) {

            "Alimentación" -> 2L
            "Transporte" -> 3L
            "Salud" -> 4L
            "Educación" -> 5L
            "Entretenimiento" -> 6L
            "Servicios" -> 7L
            else -> 8L
        }

        val usuarioId = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid ?: ""

        val movimiento = Movimiento(

            usuario_id = usuarioId,

            categoria_id = categoriaId,

            tipo = tipoMovimiento,

            monto = montoTexto.toDouble(),

            metodo_pago = metodoPago,

            descripcion = descripcion,

            fecha = fecha
        )

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                SupabaseProvider.client
                    .postgrest["movimientos"]
                    .insert(movimiento)

                Toast.makeText(
                    requireContext(),
                    "Movimiento guardado correctamente",
                    Toast.LENGTH_LONG
                ).show()

                limpiarFormulario()

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun limpiarFormulario() {

        etTitulo.text.clear()
        etDescripcion.text.clear()
        etMonto.text.clear()

        etDia.text.clear()
        etMes.text.clear()
        etAnio.text.clear()

        spCategoria.setSelection(0)
        spMetodoPago.setSelection(0)

        tipoMovimiento = "ingreso"

        btnIngreso.setBackgroundColor(Color.parseColor("#DFF5E1"))
        btnGasto.setBackgroundColor(Color.parseColor("#F3F3F3"))
    }
}