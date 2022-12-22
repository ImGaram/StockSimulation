package com.example.stock.view.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stock.data.RetrofitClient
import com.example.stock.databinding.FragmentStockBinding
import com.example.stock.view.adapter.StockAdapter
import com.example.stock.viewmodel.StockViewModel
import com.lakue.pagingbutton.OnPageSelectListener

class StockFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this, StockViewModel.Factory(
            activity?.application!!, RetrofitClient.STOCK_KEY
        ))[StockViewModel::class.java]
    }
    private lateinit var binding: FragmentStockBinding
    private lateinit var adapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelStore.clear()
        binding = FragmentStockBinding.inflate(layoutInflater)
        binding.lpbButtonlist.addBottomPageButton(188158,1)
        adapter = StockAdapter(requireContext())

        getStock(1)
        binding.lpbButtonlist.setOnPageSelectListener(object :OnPageSelectListener {
            override fun onPageBefore(now_page: Int) {
                binding.lpbButtonlist.addBottomPageButton(188158, now_page)
                getStock(now_page)
            }

            override fun onPageCenter(now_page: Int) {
                getStock(now_page)
            }

            override fun onPageNext(now_page: Int) {
                binding.lpbButtonlist.addBottomPageButton(188158, now_page)
                getStock(now_page)
            }

        })
        return binding.root
    }

    private fun getStock(number: Int) {
        // shimmer 로직
        binding.shimmerFrame.startShimmer()
        binding.shimmerFrame.visibility = View.VISIBLE
        binding.scroll.visibility = View.GONE

        // scroll view 맨 위로 올리기
        binding.scroll.post {
            binding.scroll.scrollTo(0, 0)
        }

        // get stock logic
        viewModel.loadPost(number)
        viewModel.getStockLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isEmpty()) {
                    Log.d("TAG", "getStock list is empty: ${response.size}")
                } else {
                    if (response[0].response?.header?.resultCode == "00")
                        Log.d("TAG", "getStock list size: ${response.size}")
                    Log.d("TAG", "onCreate: success")
                    adapter.clear()
                    adapter.addItem(response[0].response?.body?.items?.item!!)
                    initRecycler()
                }
            }
        }
    }

    private fun initRecycler() {
        binding.stockRecycler.adapter = adapter
        binding.stockRecycler.layoutManager = LinearLayoutManager(context)

        // shimmer 로직
        binding.shimmerFrame.stopShimmer()
        binding.shimmerFrame.visibility = View.GONE
        binding.scroll.visibility = View.VISIBLE
    }
}