package com.example.stock.view.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stock.data.RetrofitClient
import com.example.stock.data.response.news.Article
import com.example.stock.databinding.FragmentNewsBinding
import com.example.stock.view.adapter.NewsAdapter
import com.example.stock.viewmodel.NewsViewModel

class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
    private val viewModel by lazy {
        ViewModelProvider(this, NewsViewModel.Factory(RetrofitClient.NEWS_KEY))[NewsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater)

        viewModel.getNews()
        viewModel.getNewsLiveData.observe(viewLifecycleOwner) { response ->
            initRecycler(response.articles)
        }

        return binding.root
    }

    private fun initRecycler(articles: List<Article>?) {
        val adapter = NewsAdapter(articles, requireContext())
        binding.newsRecyclerView.adapter = adapter
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}