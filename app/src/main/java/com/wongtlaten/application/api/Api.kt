package com.wongtlaten.application.api

import com.midtrans.sdk.corekit.models.TransactionDetails
import com.midtrans.sdk.corekit.models.TransactionResponse
import com.midtrans.sdk.corekit.models.TransactionStatusResponse
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.wongtlaten.application.model.City
import com.wongtlaten.application.model.Cost
import retrofit2.Call
import retrofit2.http.*

interface Api {

    // fungsi untuk mendapatkan rest api prays time
    @FormUrlEncoded
    @POST("cost")
    fun getCost(
        @Field("origin") origin: String,
        @Field("destination") destination: String,
        @Field("weight") weight: Int,
        @Field("courier") courier: String,
    ): Call<Cost>

    // fungsi untuk mendapatkan rest api prays time
    @GET("city")
    fun getCity(): Call<City>

    @GET("https://api.sandbox.midtrans.com/v2/{idTransaction}/status")
    fun getStatusTransaction(@Path("idTransaction") idTransaction: String): Call<com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse>

    @GET("https://api.sandbox.midtrans.com/v2/{idTransaction}/status")
    fun getStatusTransaction2(@Path("idTransaction") idTransaction: String): Call<TransactionResponse>

}