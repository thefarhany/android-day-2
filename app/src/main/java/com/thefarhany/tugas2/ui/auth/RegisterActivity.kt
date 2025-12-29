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
import com.thefarhany.tugas2.databinding.ActivityRegisterBinding
import com.thefarhany.tugas2.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        setupListener()
    }

    private fun setupListener() = with(binding) {
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            doRegister(name, email, password, confirmPassword)
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun doRegister(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan konfirmasi tidak sama", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(name, email, password)
        userPreferences.saveUser(user)
        userPreferences.setLoggedIn(false)

        Toast.makeText(this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}