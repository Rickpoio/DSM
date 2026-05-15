package com.example.proyectodecatedra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodecatedra.adapter.MovimientoAdapter
import com.example.proyectodecatedra.data.MovimientoResponse
import com.example.proyectodecatedra.network.SupabaseProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import android.content.Context
class MisMovimientosFragment : Fragment() {

    private lateinit var rvMovimientos: RecyclerView

    private lateinit var adapter: MovimientoAdapter

    private var listaMovimientos = mutableListOf<MovimientoResponse>()

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

        rvMovimientos = view.findViewById(R.id.rvMovimientos)

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

        obtenerMovimientos()

        return view
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

                listaMovimientos.clear()

                listaMovimientos.addAll(
                    response.filter {
                        it.usuario_id == usuarioId
                    }
                )

                adapter.notifyDataSetChanged()

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

    private fun eliminarMovimiento(movimiento: MovimientoResponse) {

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

                e.printStackTrace()

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
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}