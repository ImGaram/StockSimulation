package com.example.stock.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stock.data.response.news.Article
import com.example.stock.databinding.ItemNewsBinding

class NewsAdapter(private val list: List<Article>?, private val context: Context): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list!![position])
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class ViewHolder(private val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            Glide.with(context)
                .load(article.urlToImage)
                .into(binding.newsImage)
            binding.newsTitle.text = article.title

            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                context.startActivity(intent)
            }
        }
    }
}
