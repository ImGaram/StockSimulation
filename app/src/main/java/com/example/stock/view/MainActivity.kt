package com.example.stock.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stock.R
import com.example.stock.databinding.ActivityMainBinding
import com.example.stock.view.screen.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.linear, StockFragment()).commit()
        navigationLogic()
    }

    private fun navigationLogic() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stock -> {
                    supportFragmentManager.beginTransaction().replace(R.id.linear, StockFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.stock_bookmark -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.linear, BookmarkFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.news -> {
                    supportFragmentManager.beginTransaction().replace(R.id.linear, NewsFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.linear, ProfileFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.ranking -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.linear, RankingFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}