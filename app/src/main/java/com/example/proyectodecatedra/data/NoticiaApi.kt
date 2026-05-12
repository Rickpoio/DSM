package com.example.proyectodecatedra.data
import retrofit2.Call
import retrofit2.http.*
interface NoticiaApi {
    @GET("finanzas")
    fun obtenerNoticias(): Call<List<Noticia>>

    @GET("finanzas/{id}")
    fun obtenerNoticiaPorId(@Path("id")id : String): Call<Noticia>




}