package com.example.proyectodecatedra.data

import kotlinx.serialization.Serializable

@Serializable
data class usuariosData(
    val id: String,
    val nombre: String,
    val correo: String,
    val moneda: String? = null,
    val imagen: String? = null
)
