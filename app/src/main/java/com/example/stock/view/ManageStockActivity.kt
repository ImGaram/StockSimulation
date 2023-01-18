package com.example.stock.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stock.databinding.ActivityManageStockBinding

class ManageStockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitManageStock.setOnClickListener { finish() }
    }
}