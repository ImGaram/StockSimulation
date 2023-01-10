package com.example.stock.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.stock.data.firebase.BuyingItem
import com.example.stock.data.firebase.User
import com.example.stock.data.response.stock.Item
import com.example.stock.databinding.DialogSellStockBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellStockDialog(
    context: Context,
    private val item: Item,
    private val presentPrice: Int
) : Dialog(context) {
    private lateinit var binding: DialogSellStockBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogSellStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
    }

    private fun setView() = with(binding) {
        setCancelable(false)

        database.child("buying").child(uid).child(item.itmsNm).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(BuyingItem::class.java)
                // set number picker
                sellWeekPicker.maxValue = data!!.buyingCount
                sellWeekPicker.minValue = 0
                sellWeekPicker.value = 0
                sellWeekPicker.setOnValueChangedListener { numberPicker, old, new ->
                    sellBtn.setOnClickListener {
                        if (new == data.buyingCount) {
                            database.child("buying").child(uid).child(data.itemName).removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful)
                                        settleMoney(new)
                                }
                        } else {
                            database.child("buying").child(uid).child(data.itemName).setValue(BuyingItem(data.itemName, data.buyingCount - new))
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful)
                                        settleMoney(new)
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })


        sellCancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun settleMoney(new: Int) {
        database.child("user").child(uid).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(User::class.java)

                database.child("user").child(uid).child("money").setValue(data!!.money + new*presentPrice)
                    .addOnCompleteListener {
                        Toast.makeText(context, "주식을 판매했습니다.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}