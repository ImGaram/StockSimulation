package com.example.stock.view.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stock.data.firebase.RankItem
import com.example.stock.databinding.FragmentRankingBinding
import com.example.stock.view.adapter.RankingAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingBinding
    private val fireStore = FirebaseFirestore.getInstance()
    private lateinit var adapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(layoutInflater)
        adapter = RankingAdapter(requireContext())

        getRank()
        return binding.root
    }

    private fun getRank() {
        fireStore.collection("rank").orderBy("totalMoney", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (i in querySnapshot.documents) {
                    val data = i.toObject(RankItem::class.java)
                    if (data != null) adapter.add(data)
                }

                initRecycler()
            }
    }

    private fun initRecycler() {
        binding.rankRecyclerView.adapter = adapter
        binding.rankRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}