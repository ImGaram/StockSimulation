package com.example.stock.data

data class ChartData(
    val createdAt: Long,    // 차트의 x죄표
    val open: Float,        // 시가
    val close: Float,       // 종가
    val shadowHigh: Float,  // 최고가
    val shadowLow: Float    // 최저가
)
