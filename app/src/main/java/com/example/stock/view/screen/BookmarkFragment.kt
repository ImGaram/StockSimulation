package com.example.stock.view.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stock.data.response.stock.Item
import com.example.stock.databinding.FragmentBookmarkBinding
import com.example.stock.view.adapter.BookmarkAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: BookmarkAdapter
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance()
        adapter = BookmarkAdapter(requireContext())

        binding.bookmarkShimmerFrame.startShimmer()
        binding.bookmarkShimmerFrame.visibility = View.VISIBLE
        binding.bookmarkRecyclerView.visibility = View.GONE

        database.reference.child("bookmark").child(uid).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot in snapshot.children) {
                    val item = dataSnapShot.getValue(Item::class.java)
                    if (item != null) adapter.add(item)
                }

                initBookmarkRecycler()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return binding.root
    }

    private fun initBookmarkRecycler() {
        binding.bookmarkRecyclerView.adapter = adapter
        binding.bookmarkRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.bookmarkShimmerFrame.stopShimmer()
        binding.bookmarkShimmerFrame.visibility = View.GONE
        binding.bookmarkRecyclerView.visibility = View.VISIBLE
    }
}