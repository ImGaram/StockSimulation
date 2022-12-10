package com.example.stock.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stock.data.response.StockResponse
import com.example.stock.repository.StockRepository
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository,
    private val apikey: String,
    private val pageNo: Int
): ViewModel() {
    private val _getStockLiveData: MutableLiveData<StockResponse> = MutableLiveData()
    val getStockLiveData: MutableLiveData<StockResponse> get() = _getStockLiveData

    init {
        viewModelScope.launch {
            _getStockLiveData.value = repository.getStock(apikey, pageNo)
        }
    }

    class Factory(private val application: Application, private val apikey: String, private val pageNo: Int): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StockViewModel(StockRepository(application), apikey, pageNo) as T
        }
    }
}