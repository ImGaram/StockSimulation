package com.example.stock.retrofit

import com.example.stock.data.response.stock.StockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockService {
    @GET("/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?resultType=json")
    suspend fun getStock(
        @Query("serviceKey") serviceKey: String,    // 인증키
        @Query("pageNo") pageNo: Int                // 페이지 번호(주식 리스트용)
    ): Response<StockResponse>
}