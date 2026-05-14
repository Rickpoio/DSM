package com.example.proyectodecatedra.data

import kotlinx.serialization.Serializable

@Serializable
data class Movimiento(

    val usuario_id: String,

    val categoria_id: Long,

    val tipo: String,

    val monto: Double,

    val metodo_pago: String,

    val descripcion: String,

    val fecha: String
)