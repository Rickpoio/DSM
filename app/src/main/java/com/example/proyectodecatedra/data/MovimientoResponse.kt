package com.example.proyectodecatedra.data

import kotlinx.serialization.Serializable

@Serializable
data class MovimientoResponse(

    val id: Int,

    val usuario_id: String? = null,

    val categoria_id: Long? = null,

    val tipo: String? = null,

    val monto: Double? = 0.0,

    val metodo_pago: String? = null,

    val descripcion: String? = null,

    val fecha: String? = null,
    val fecha_creacion: String?
)