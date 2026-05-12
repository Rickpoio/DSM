package com.example.proyectodecatedra.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.proyectodecatedra.BuildConfig
object RetrofitClient {
    private const val BASE_URL = BuildConfig.noticiasApiUrl
    val instance: NoticiaApi by lazy {
        val retrofit=Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(NoticiaApi::class.java)
    }
}