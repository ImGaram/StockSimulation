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
import com.example.stock.databinding.DialogBuyStockBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BuyStockDialog(context: Context, private val price: String, private val itemName: String): Dialog(context) {
    private lateinit var binding: DialogBuyStockBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogBuyStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() = with(binding) {
        val intPrice = price.substring(0, price.length -1).toInt()

        setCancelable(false)    // 빈 공간 클릭해도 다이얼로그 안없어지게
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogTitle.text = "${itemName}을(를) 구매하시겠습니까?"
        // 텍스트가 변경될때마다 호출
        stockCount.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("TAG", "beforeTextChanged: before")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (stockCount.text.toString().isEmpty()) {
                    totalPrice.text = "총 0원"
                } else totalPrice.text = "총 ${stockCount.text.toString().toInt() * intPrice}원"
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("TAG", "afterTextChanged: after")
            }
        })

        buyStock.setOnClickListener {
            database.child("user").child(auth.currentUser?.uid.toString())
                .child("money").setValue(stockCount.text.toString().toInt() * intPrice).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "구매가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
        }

        cancelBuy.setOnClickListener {
            dismiss()
        }
    }
}