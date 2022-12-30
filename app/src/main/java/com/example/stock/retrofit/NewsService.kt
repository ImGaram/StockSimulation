package com.example.stock.retrofit

import com.example.stock.data.response.news.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("top-headlines?country=kr")
    fun news(
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}