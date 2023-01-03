package com.example.stock.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stock.R
import com.example.stock.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pw = binding.password.text.toString()
            if (email.isEmpty() && pw.isEmpty())
                Toast.makeText(this, "이메일이랑 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                signInLogic(email, pw)
            }
        }
    }

    private fun signInLogic(email: String, pw: String) {
        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}