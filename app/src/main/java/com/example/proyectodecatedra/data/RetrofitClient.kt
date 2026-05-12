package com.example.proyectodecatedra.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL ="https://69daaea726585bd92dd40a70.mockapi.io/"
    val instance: NoticiaApi by lazy {
        val retrofit=Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(NoticiaApi::class.java)
    }
}