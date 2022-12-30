package com.example.stock.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stock.data.response.news.NewsResponse
import com.example.stock.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository,
    private val apikey: String
): ViewModel() {
    private val _getNewsLiveData: MutableLiveData<NewsResponse> = MutableLiveData()
    val getNewsLiveData: MutableLiveData<NewsResponse> get() = _getNewsLiveData

    fun getNews() {
        viewModelScope.launch {
            _getNewsLiveData.value = repository.getNews(apikey)
        }
    }

    class Factory(private val apikey: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsViewModel(NewsRepository(), apikey) as T
        }
    }
}