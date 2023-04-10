package com.wongtlaten.application.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(InterceptorApi())
    }.build()

    //mendefinisikan BASE_URL
    private const val BASE_URL = "https://api.rajaongkir.com/starter/"

    //menggunakan library retrofit untuk digunakan pada API
    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(Api::class.java)
    }

}