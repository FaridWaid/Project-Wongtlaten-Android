package com.wongtlaten.application.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class InterceptorApi: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = Credentials.basic("SB-Mid-server-3P1OrGxFFyjf9md1-yhW-tC_", "")
        val request = chain.request()
            .newBuilder()
            .addHeader("key", "191c820a86dd458cb852dd74a36a9264")
            .addHeader("Content-Type", "application/json")
            .addHeader("x-api-key", "SB-Mid-client-W_fr1Eg_qXdR7Ssz")
            .addHeader("Authorization", credential)
            .build()
        return chain.proceed(request)
    }
}