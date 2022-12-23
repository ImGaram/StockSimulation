package com.example.stock.data.response

import java.io.Serializable

data class Item(
    val basDt: String,
    val clpr: String,
    val fltRt: String,
    val hipr: String,
    val isinCd: String,
    val itmsNm: String,
    val lopr: String,
    val lstgStCnt: String,
    val mkp: String,
    val mrktCtg: String,
    val mrktTotAmt: String,
    val srtnCd: String,
    val trPrc: String,
    val trqu: String,
    val vs: String
): Serializable