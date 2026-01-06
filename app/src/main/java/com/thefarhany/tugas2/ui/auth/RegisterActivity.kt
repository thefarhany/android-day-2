package com.thefarhany.tugas2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.data.repository.AuthRepository
import com.thefarhany.tugas2.databinding.ActivityRegisterBinding
import com.thefarhany.tugas2.ui.home.HomeActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupListener()
    }

    private fun setupListener() = with(binding) {
        btnRegister.setOnClickListener {
            val username = etName.text.toString().trim()
            val phoneNumber = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            doRegister(username, phoneNumber, email, password, confirmPassword)
        }

        tvLogin.setOnClickListener { finish() }
    }

    private fun doRegister(
        username: String,
        phoneNumber: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan konfirmasi tidak sama", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val res = authRepository.register(username, phoneNumber, email, password)
                sessionManager.saveToken(res.token)

                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    e.message ?: "Register gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
