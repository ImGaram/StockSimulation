package com.example.stock.view.screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.stock.R
import com.example.stock.data.RetrofitClient
import com.example.stock.databinding.FragmentStockBinding
import com.example.stock.view.MainActivity
import com.example.stock.viewmodel.StockViewModel

class StockFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this, StockViewModel.Factory(activity?.application!!, RetrofitClient.STOCK_KEY, pageNo))[StockViewModel::class.java]
    }
    private val pageNo = 1
    private lateinit var binding: FragmentStockBinding

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentStockBinding.inflate(layoutInflater)

       viewModel.getStockLiveData.observe(viewLifecycleOwner) { response ->
           if (response != null) Log.d("TAG", "onCreate: success")
       }

        return binding.root
    }
}