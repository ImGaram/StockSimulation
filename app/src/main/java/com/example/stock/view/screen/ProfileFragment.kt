package com.example.stock.view.screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stock.databinding.FragmentProfileBinding
import com.example.stock.data.firebase.User
import com.example.stock.view.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().reference

        setView()
        binding.logoutLayout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, SignInActivity::class.java))
            activity?.finish()
        }

        return binding.root
    }

    private fun setView() {
        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    val dec = DecimalFormat("#,###")

                    binding.profileName.text = data?.name
                    binding.profileEmail.text = data?.email
                    binding.myMoney.text = "${dec.format(data?.money)}원"
                    binding.ratePercent.text = ""   // 바뀐 값 - 100,000 * 100
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }
}