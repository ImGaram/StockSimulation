package com.example.stock.repository

import com.example.stock.data.response.stock.StockResponse
import com.example.stock.retrofit.StockObject

class StockRepository {
    suspend fun getStock(apikey: String, pageNo: Int): StockResponse {
        val response = StockObject.stockService.getStock(apikey, pageNo)
        return if (response.isSuccessful)
            response.body() as StockResponse
        else StockResponse(null)
    }

    suspend fun getStockInfo(apikey: String, itmsNm: String, numOfRows: Int): StockResponse {
        val response = StockObject.stockInfoService.getOneStock(numOfRows, itmsNm, apikey)
        return if (response.isSuccessful)
            response.body() as StockResponse
        else StockResponse(null)
    }
}