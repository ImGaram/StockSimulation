package com.example.stock.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stock.R
import com.example.stock.data.firebase.RankItem
import com.example.stock.databinding.ItemRankingBinding
import java.text.DecimalFormat

class RankingAdapter(private val context: Context): RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    private val list = arrayListOf<RankItem>()
    val dec = DecimalFormat("#,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemRankingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(rankItem: RankItem) {
            when (bindingAdapterPosition.plus(1)) {
                1 -> {
                    binding.numberRank.setTextColor(Color.parseColor("#ffd700"))
                    binding.numberRank.text = bindingAdapterPosition.plus(1).toString()
                }
                2 -> {
                    binding.numberRank.setTextColor(Color.parseColor("#c0c0c0"))
                    binding.numberRank.text = bindingAdapterPosition.plus(1).toString()
                }
                3 -> {
                    binding.numberRank.setTextColor(Color.parseColor("#800000"))
                    binding.numberRank.text = bindingAdapterPosition.plus(1).toString()
                }
                else -> {
                    binding.numberRank.setTextColor(Color.BLACK)
                    binding.numberRank.text = bindingAdapterPosition.plus(1).toString()
                }
            }
            binding.rankUserEmail.text = rankItem.email
            binding.rankUserName.text = rankItem.name
            binding.userTotalMoney.text = dec.format(rankItem.totalMoney)
            Glide.with(context)
                .load(rankItem.profile)
                .into(binding.rankUserProfile)
        }
    }

    fun add(item: RankItem) {
        list.add(item)
    }
}
