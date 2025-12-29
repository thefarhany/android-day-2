package com.thefarhany.tugas2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thefarhany.tugas2.R
import com.thefarhany.tugas2.data.local.UserPreferences
import com.thefarhany.tugas2.databinding.ActivityLoginBinding
import com.thefarhany.tugas2.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        setupListener()
    }

    private fun setupListener() = with(binding) {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            doLogin(email, password)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun doLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val savedUser = userPreferences.getUser()
        if (savedUser == null) {
            Toast.makeText(this, "Belum ada akun, silakan register dulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (email == savedUser.email && password == savedUser.password) {
            userPreferences.setLoggedIn(true)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show()
        }
    }
}