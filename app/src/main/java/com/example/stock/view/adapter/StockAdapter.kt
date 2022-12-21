package com.example.stock.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stock.data.response.Item
import com.example.stock.databinding.ItemStockBinding

class StockAdapter(): RecyclerView.Adapter<StockAdapter.ViewHolder>() {
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
        fun bind(item: Item) {
            binding.clpr.text = item.clpr+"원"
            if (item.vs.toInt() > 0) binding.vs.text = "전일 대비 ${item.vs}% 상승"
            else binding.vs.text = "전일 대비 ${item.vs}% 하락"
            binding.itemsName.text = item.itmsNm
        }
    }

    fun addItem(item: List<Item>) {
        list.addAll(item)
    }

    fun clear() {
        list.clear()
    }
}