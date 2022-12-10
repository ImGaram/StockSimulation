package com.example.stock.repository

import android.app.Application
import android.util.Log
import com.example.stock.data.response.StockResponse
import com.example.stock.retrofit.StockObject

class StockRepository(application: Application) {
    companion object {
        private var instance: StockRepository? = null

        fun getInstance(application: Application): StockRepository? {
            if (instance != null) instance = StockRepository(application)
            return instance
        }
    }

    suspend fun getStock(apikey: String, pageNo: Int): StockResponse {
        val response = StockObject.stockService.getStock(apikey, pageNo)
        return if (response.isSuccessful)
            response.body() as StockResponse
        else StockResponse(null)
    }
}