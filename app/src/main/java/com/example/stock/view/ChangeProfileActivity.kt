package com.example.stock.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.stock.data.firebase.User
import com.example.stock.databinding.ActivityChangeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ChangeProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeProfileBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitImage.setOnClickListener { finish() }
        binding.changeProfileImage.setOnClickListener { changeImage() }
        binding.changeUserNameBtn.setOnClickListener {
            val name = binding.userNameText.text.toString()
            if (name.isEmpty())
                Toast.makeText(this, "유저 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                database.child("user").child(auth).child("name")
                    .setValue(binding.userNameText.text.toString())
                Toast.makeText(this, "이름 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        setUser()
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    Glide.with(this)
                        .load(uri)
                        .into(binding.currentImage)
                    uploadImage(uri)
                }
            }
        }

    private fun uploadImage(uri: Uri) {
        storage.child("profile/$auth").putFile(uri).addOnSuccessListener {
            storage.child("profile/$auth").downloadUrl.addOnSuccessListener { url ->
                database.child("user").child(auth).child("profile").setValue(url.toString()).addOnSuccessListener {
                    Toast.makeText(this, "프로필 사진 설정을 완료했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun changeImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        getContent.launch(intent)
    }

    private fun setUser() {
        database.child("user").child(auth)
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(User::class.java)

                    Glide.with(this@ChangeProfileActivity)
                        .load(value?.profile)
                        .into(binding.currentImage)
                    binding.userNameText.setText(value?.name)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}