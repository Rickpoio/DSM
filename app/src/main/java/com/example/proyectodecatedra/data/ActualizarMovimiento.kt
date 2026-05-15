package com.example.proyectodecatedra.data

import kotlinx.serialization.Serializable

@Serializable
data class ActualizarMovimiento(

    val descripcion: String,

    val monto: Double,

    val metodo_pago: String,

    val fecha: String,

    val tipo: String
)