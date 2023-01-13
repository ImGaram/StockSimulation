package com.example.stock.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stock.R
import com.example.stock.data.RetrofitClient
import com.example.stock.data.firebase.BuyingItem
import com.example.stock.data.firebase.RankItem
import com.example.stock.databinding.ActivityMainBinding
import com.example.stock.data.firebase.User
import com.example.stock.view.screen.*
import com.example.stock.viewmodel.StockInfoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val fireStore = FirebaseFirestore.getInstance()

    private val viewModel by lazy {
        ViewModelProvider(this, StockInfoViewModel.Factory(
            RetrofitClient.STOCK_KEY, 1, itemName
        ))[StockInfoViewModel::class.java]
    }
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.linear, StockFragment()).commit()

        getUserInfo()
        navigationLogic()
    }

    private fun setRanking(user: User) {
        database.child("buying").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        Log.d("TAG", "onDataChange: value is null")
                    } else {
                        for (dataSnapshot in snapshot.children) {
                            val item = dataSnapshot.getValue(BuyingItem::class.java)

                            getTotal(item) {
                                fireStore.collection("rank").document(auth.currentUser?.uid.toString())
                                    .set(RankItem(user.name, user.email, user.profile, it + user.money))
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    // 유저의 총 금액을 불러오기 위한 유저 정보 불러오기
    private fun getUserForRank() {
        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)

                    if (data != null) {
                        setRanking(data)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun getUserInfo() {
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

    private fun getTotal(item: BuyingItem?, value: (Int) -> Unit): Int {
        var total = 0

        itemName = item?.itemName.toString()
        viewModel.getStockInfo()
        viewModel.getStockInfoLiveData.observe(this) { response ->
            val clpr = response.response?.body?.items?.item!![0].clpr
            total += item?.buyingCount!! * clpr.toInt()
            value(total)
        }
        return total
    }

    // activity 가 사용자와 상호작용하기 전에 호출
    override fun onResume() {
        super.onResume()

        // 코인 정보 갱신
        getUserInfo()
    }

    // activity 가 보이기 전에 불림
    override fun onStart() {
        super.onStart()
        getUserForRank()
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