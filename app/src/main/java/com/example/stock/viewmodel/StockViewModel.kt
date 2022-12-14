package com.example.stock.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stock.data.response.stock.StockResponse
import com.example.stock.repository.StockRepository
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository,
    private val apikey: String
): ViewModel() {
    private val _getStockLiveData: MutableLiveData<ArrayList<StockResponse>> = MutableLiveData()
    val getStockLiveData: MutableLiveData<ArrayList<StockResponse>> get() = _getStockLiveData
    private val list = arrayListOf<StockResponse>()

    fun loadPost(number: Int) {
        viewModelScope.launch {
            list.add(repository.getStock(apikey, number))
            Log.d("TAG", "loadPost vm list: $list")
            _getStockLiveData.value = list
            list.clear()
        }
    }

    class Factory(private val apikey: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StockViewModel(StockRepository(), apikey) as T
        }
    }
}