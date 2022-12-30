package com.example.stock.retrofit

import com.example.stock.data.RetrofitClient
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsObject {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(RetrofitClient.BASE_URL_NEWS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    val newsService: NewsService = retrofit.create(NewsService::class.java)
}