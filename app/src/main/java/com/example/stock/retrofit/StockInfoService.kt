package com.example.stock.retrofit

import com.example.stock.data.response.StockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockInfoService {
    @GET("/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?resultType=json")
    suspend fun getOneStock(
        @Query("numOfRows") numOfRows: Int,
        @Query("itmsNm") itmsNm: String,
        @Query("serviceKey") serviceKey: String
    ): Response<StockResponse>
}