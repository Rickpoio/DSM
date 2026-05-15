package com.example.proyectodecatedra.network

import com.example.proyectodecatedra.data.Noticia
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NoticiaApi {
    @GET("finanzas")
    fun obtenerNoticias(): Call<List<Noticia>>

    @GET("finanzas/{id}")
    fun obtenerNoticiaPorId(@Path("id")id : String): Call<Noticia>




}