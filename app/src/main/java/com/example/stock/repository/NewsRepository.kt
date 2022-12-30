package com.example.stock.repository

import com.example.stock.data.response.news.NewsResponse
import com.example.stock.retrofit.NewsObject

class NewsRepository {
    suspend fun getNews(apikey: String): NewsResponse {
        val response = NewsObject.newsService.news(apikey)
        return if (response.isSuccessful)
            response.body() as NewsResponse
        else NewsResponse(null, null, null)
    }
}