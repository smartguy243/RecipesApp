package com.smartguy.recipesapp.data.network

import okhttp3.Interceptor

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().let { req ->
            val url = req.url.newBuilder()
                .addQueryParameter("apiKey", apiKey)
                .build()
            req.newBuilder().url(url).build()
        }
    )
}
