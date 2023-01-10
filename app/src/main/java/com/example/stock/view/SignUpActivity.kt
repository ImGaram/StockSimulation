package com.example.stock.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stock.data.firebase.BookmarkItem
import com.example.stock.databinding.ActivitySignUpBinding
import com.example.stock.data.firebase.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    private val fireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signUpBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val pw = binding.pwEditText.text.toString()
            if (email.isEmpty() || name.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호, 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (pw != binding.pwCheckEditText.text.toString())
                    Toast.makeText(this, "비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT).show()
                else signUpLogic(email, pw, name)
            }
        }

        binding.signUpCancelBtn.setOnClickListener { finish() }
    }

    private fun signUpLogic(email: String, pw: String, name: String) {
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val storage = FirebaseStorage.getInstance().reference.child("profile/basic.png")
                storage.downloadUrl.addOnSuccessListener {
                    val user = User(email, pw, name, it.toString(), 100000)

                    database.child("user").child(auth.currentUser?.uid.toString()).setValue(user).addOnSuccessListener {
                        // 북마크용 데이터 추가
                        fireStore.collection("bookmark").document(auth.currentUser?.uid.toString()).set(BookmarkItem())

                        Toast.makeText(this, "회원가입을 완료했습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SignInActivity::class.java))
                    }
                }
            } else {
                Toast.makeText(this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}