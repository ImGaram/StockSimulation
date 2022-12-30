package com.example.stock.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stock.data.response.stock.StockResponse
import com.example.stock.repository.StockRepository
import kotlinx.coroutines.launch

class StockInfoViewModel(
    private val repository: StockRepository,
    private val numOfRows: Int,
    private val itmsNm: String,
    private val serviceKey: String
): ViewModel() {
    private val _getStockInfoLiveData: MutableLiveData<StockResponse> = MutableLiveData()
    val getStockInfoLiveData: MutableLiveData<StockResponse> get() = _getStockInfoLiveData

    fun getStockInfo() {
        viewModelScope.launch {
            _getStockInfoLiveData.value = repository.getStockInfo(serviceKey, itmsNm, numOfRows)
        }
    }

    class Factory(private val application: Application, private val apikey: String, private val numOfRows: Int,
                  private val itmsNm: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StockInfoViewModel(StockRepository(application), numOfRows, itmsNm, apikey) as T
        }
    }
}