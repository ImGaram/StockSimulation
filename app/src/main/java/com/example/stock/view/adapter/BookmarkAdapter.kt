package com.example.stock.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stock.R
import com.example.stock.data.firebase.BookmarkItem
import com.example.stock.data.response.stock.Item
import com.example.stock.databinding.ItemStockBinding
import com.example.stock.view.StockInfoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class BookmarkAdapter(private val context: Context) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    private val list: ArrayList<Item> = arrayListOf()
    private val database = FirebaseDatabase.getInstance().reference
    val fireStore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.image.setOnClickListener {
            val tsDoc = fireStore.collection("bookmark").document(auth)
            fireStore.runTransaction { ts ->
                val bookmark = ts.get(tsDoc).toObject(BookmarkItem::class.java)

                if (bookmark!!.bookmark.containsKey(item.itmsNm)) {
                    Log.d("TAG", "bind if")
                    bookmark.bookmark.remove(item.itmsNm)
                    database.child("bookmark").child(auth).child(item.itmsNm).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "찜한 주식에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            list.removeAt(position)     // 불러온 리스트의 해당 인덱스의 데이터 삭제
                            notifyItemRemoved(position) // 해당 위치의 아이템 삭제
                        }
                }
                /*else {
                    Log.d("TAG", "bind else")
                    bookmark.bookmark[item.itmsNm] = true
                    database.child("bookmark").child(auth).child(item.itmsNm).setValue(item)
                        .addOnSuccessListener {
                            Toast.makeText(context, "찜한 주식에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                            notifyItemChanged(position)
                        }
                }*/

                ts.set(tsDoc, bookmark)
            }
        }

        setBookmark(holder.image, item)
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemStockBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        val image = binding.favoriteImage
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

            itemView.setOnClickListener {
                val intent = Intent(context, StockInfoActivity::class.java)
                intent.putExtra("name", item.itmsNm)
                intent.putExtra("info", item)
                context.startActivity(intent)
            }
        }
    }

    private fun setBookmark(favoriteImage: ImageView, item: Item) {
        fireStore.collection("bookmark").document(auth)
            .get()
            .addOnSuccessListener {
                val data = it.toObject(BookmarkItem::class.java)
                if (data!!.bookmark.containsKey(item.itmsNm)) {
                    favoriteImage.setImageResource(R.drawable.star_select)
                } else {
                    favoriteImage.setImageResource(R.drawable.star_unselect)
                }
            }
    }

    fun add(item: Item) {
        list.add(item)
    }
}