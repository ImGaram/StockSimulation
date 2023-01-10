package com.example.stock.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.stock.data.firebase.BuyingItem
import com.example.stock.data.firebase.User
import com.example.stock.data.response.stock.Item
import com.example.stock.databinding.DialogBuyStockBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuyStockDialog(context: Context, private val item: Item): Dialog(context) {
    private lateinit var binding: DialogBuyStockBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val clpr = item.clpr.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogBuyStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() = with(binding) {
        setCancelable(false)    // 빈 공간 클릭해도 다이얼로그 안없어지게
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogTitle.text = "${item.itmsNm}을(를) 구매하시겠습니까?"
        // 텍스트가 변경될때마다 호출
        stockCount.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("TAG", "beforeTextChanged: before")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (stockCount.text.toString().isEmpty()) {
                    totalPrice.text = "총 0원"
                } else totalPrice.text = "총 ${stockCount.text.toString().toInt() * clpr}원"
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("TAG", "afterTextChanged: after")
            }
        })

        buyStock.setOnClickListener {
            // 유저 돈 값 변경, 매수 목록에 추가
            val totalPrice = stockCount.text.toString().toInt() * clpr
            changeMoney(totalPrice)
        }

        cancelBuy.setOnClickListener {
            dismiss()
        }
    }

    private fun changeMoney(total: Int) {
        val user = auth.currentUser?.uid.toString()
        database.child("user").child(user).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(User::class.java)
                val res = value?.money?.minus(total)

                database.child("user").child(user)
                    .child("money").setValue(res).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            addBuyingList()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun addBuyingList() = with(binding) {
        val buyingItem = BuyingItem(item.itmsNm, stockCount.text.toString().toInt())
        database.child("buying").child(auth.currentUser?.uid.toString()).child(item.itmsNm).setValue(buyingItem)
            .addOnSuccessListener {
                Toast.makeText(context, "구매가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                dismiss()
            }
    }
}