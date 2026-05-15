package com.example.proyectodecatedra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectodecatedra.data.MovimientoResponse
import com.example.proyectodecatedra.data.usuariosData
import com.example.proyectodecatedra.network.SupabaseProvider
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private lateinit var imgPerfil: CircleImageView

    private lateinit var tvNombre: TextView
    private lateinit var tvAhorro: TextView
    private lateinit var tvMaximo: TextView

    private lateinit var btnEditar: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_perfil,
            container,
            false
        )

        imgPerfil =
            view.findViewById(R.id.imgPerfil)

        tvNombre =
            view.findViewById(R.id.tvNombre)

        tvAhorro =
            view.findViewById(R.id.tvAhorro)

        tvMaximo =
            view.findViewById(R.id.tvMaximo)

        btnEditar =
            view.findViewById(R.id.btnEditar)

        obtenerDatosPerfil()

        configurarBotonEditar()

        return view
    }

    private fun obtenerDatosPerfil() {

        val usuarioId = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid ?: ""

        if (usuarioId.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                // OBTENER USUARIO

                val usuarios = SupabaseProvider.client
                    .from("usuarios")
                    .select()
                    .decodeList<usuariosData>()

                val usuario = usuarios.find {
                    it.id == usuarioId
                }

                if (usuario != null) {

                    tvNombre.text =
                        usuario.nombre
                }

                // OBTENER MOVIMIENTOS

                val movimientos = SupabaseProvider.client
                    .from("movimientos")
                    .select()
                    .decodeList<MovimientoResponse>()

                val movimientosUsuario =
                    movimientos.filter {
                        it.usuario_id == usuarioId
                    }

                var totalIngresos = 0.0

                var totalGastos = 0.0

                movimientosUsuario.forEach { mov ->

                    val monto =
                        mov.monto ?: 0.0

                    if (
                        mov.tipo?.lowercase() == "ingreso"
                    ) {

                        totalIngresos += monto
                    }

                    else if (
                        mov.tipo?.lowercase() == "gasto"
                    ) {

                        totalGastos += monto
                    }
                }

                val balanceFinal =
                    totalIngresos - totalGastos

                tvAhorro.text =
                    "Total ahorrado: $%.2f"
                        .format(balanceFinal)

                tvMaximo.text =
                    "Máximo ahorrado: $%.2f"
                        .format(totalIngresos)

            } catch (e: Exception) {

                e.printStackTrace()

                if (isAdded) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun configurarBotonEditar() {

        btnEditar.setOnClickListener {

            // Aqui puedes abrir editar perfil
        }
    }
}