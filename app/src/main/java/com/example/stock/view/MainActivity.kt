package com.example.stock.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.stock.R
import com.example.stock.databinding.ActivityMainBinding
import com.example.stock.data.firebase.User
import com.example.stock.view.screen.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserInfo()
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

    private fun getUserInfo() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    val dec = DecimalFormat("#,###")
                    binding.coin.text = "${dec.format(data?.money)}원"
                    binding.userName.text = data?.name
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    // activity 가 사용자와 상호작용하기 전에 호출
    override fun onResume() {
        super.onResume()

        // 코인 정보 갱신
        getUserInfo()
    }
}