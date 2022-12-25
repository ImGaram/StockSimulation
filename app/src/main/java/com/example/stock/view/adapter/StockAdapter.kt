package com.example.stock.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stock.data.response.Item
import com.example.stock.databinding.ItemStockBinding
import com.example.stock.view.StockInfoActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

class StockAdapter(private val context: Context) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {
    private val list: ArrayList<Item> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemStockBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Item) {
            val dec = DecimalFormat("#,###")

            binding.clpr.text = "${dec.format(item.clpr.toInt())}원"
            if (item.fltRt.toDouble() > 0) {
                binding.clpr.setTextColor(Color.RED)
                if (item.fltRt.toDouble() < 1) {
                    binding.fltRt.text = "전일 대비 0${item.fltRt}% 상승"
                } else binding.fltRt.text = "전일 대비 ${item.fltRt}% 상승"
            } else if (item.fltRt.toDouble() < 0) {
                binding.clpr.setTextColor(Color.BLUE)
                if (item.fltRt.toDouble() > -1) {
                    val fltRt = item.fltRt
                    val res = fltRt.substring(0,1) + "0" + fltRt.substring(1)

                    binding.fltRt.text = "전일 대비 $res% 하락"
                } else binding.fltRt.text = "전일 대비 ${item.fltRt}% 하락"
            } else binding.fltRt.text = "전일 대비 유지"
            binding.itemsName.text = item.itmsNm
            setBookmark(binding, item)

            itemView.setOnClickListener {
                val temp = binding.bookmarkAbleView.visibility == View.VISIBLE

                val intent = Intent(context, StockInfoActivity::class.java)
                intent.putExtra("name", item.itmsNm)
                intent.putExtra("info", item)
                intent.putExtra("bookmark", temp)
                context.startActivity(intent)
            }
        }
    }

    private fun setBookmark(binding: ItemStockBinding, item: Item) {
        FirebaseDatabase.getInstance().reference.child("bookmark").child(item.itmsNm).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(Item::class.java)
                    if (data != null) // bookmark 가 되어 있음
                        binding.bookmarkAbleView.visibility = View.VISIBLE
                    else
                        binding.bookmarkAbleView.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun addItem(item: List<Item>) {
        list.addAll(item)
    }

    fun clear() {
        list.clear()
    }
}