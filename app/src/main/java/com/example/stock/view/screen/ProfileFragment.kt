package com.example.stock.view.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.stock.data.RetrofitClient
import com.example.stock.data.firebase.BuyingItem
import com.example.stock.data.firebase.User
import com.example.stock.databinding.FragmentProfileBinding
import com.example.stock.view.ChangeProfileActivity
import com.example.stock.view.SignInActivity
import com.example.stock.viewmodel.StockInfoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
    private val dec = DecimalFormat("#,###")

    private val viewModel by lazy {
        ViewModelProvider(this, StockInfoViewModel.Factory(
            RetrofitClient.STOCK_KEY, 1, itemName
        ))[StockInfoViewModel::class.java]
    }
    private lateinit var itemName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().reference

        setView()
        binding.changeProfileLayout.setOnClickListener {
            startActivity(Intent(context, ChangeProfileActivity::class.java))
            
        }

        binding.logoutLayout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, SignInActivity::class.java))
            activity?.finish()
        }

        binding.changeProfileLayout.setOnClickListener {
            startActivity(Intent(context, ChangeProfileActivity::class.java))
        }

        return binding.root
    }

    private fun setView() {
        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)

                    binding.profileName.text = data?.name
                    binding.profileEmail.text = data?.email
                    binding.myMoney.text = "${dec.format(data?.money)}원"
                    setTotalMoney(data?.money)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun setTotalMoney(money: Int?) {
        database.child("buying").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        binding.totalMoney.text = "${dec.format(money)}원"
                    } else {
                        for (i in snapshot.children) {
                            val item = i.getValue(BuyingItem::class.java)
                            Log.d("TAG", "onDataChange profile for loop: $item")

                            getTotalMoney(item) {
                                binding.totalMoney.text = "${dec.format(money!! + it)}원"
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun getTotalMoney(item: BuyingItem?, value: (Int) -> Unit): Int {
        var total = 0

        itemName = item?.itemName.toString()
        viewModel.getStockInfo()
        viewModel.getStockInfoLiveData.observe(viewLifecycleOwner) { response ->
            val clpr = response.response?.body?.items?.item!![0].clpr
            total += item?.buyingCount!! * clpr.toInt()
            value(total)
        }
        return total
    }

    override fun onStart() {
        super.onStart()

        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(User::class.java)

                    Glide.with(context!!)
                        .load(item?.profile)
                        .into(binding.profileImage)
                    binding.profileName.text = item?.name
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }
}