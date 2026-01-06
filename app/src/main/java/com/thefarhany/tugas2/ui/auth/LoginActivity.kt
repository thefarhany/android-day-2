package com.thefarhany.tugas2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.data.repository.AuthRepository
import com.thefarhany.tugas2.databinding.ActivityLoginBinding
import com.thefarhany.tugas2.ui.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

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

        lifecycleScope.launch {
            try {
                val res = authRepository.login(email, password)
                sessionManager.saveToken(res.token)

                val homeIntent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(homeIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}