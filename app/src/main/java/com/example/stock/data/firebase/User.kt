package com.example.stock.data.firebase

data class User(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val profile: String = "",
    val money: Int = 0
)
