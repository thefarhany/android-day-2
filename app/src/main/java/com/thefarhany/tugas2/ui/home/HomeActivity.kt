package com.thefarhany.tugas2.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thefarhany.tugas2.R
import com.thefarhany.tugas2.data.local.UserPreferences
import com.thefarhany.tugas2.databinding.ActivityHomeBinding
import com.thefarhany.tugas2.ui.auth.LoginActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        showUserName()
        setupListener()
    }

    private fun showUserName() {
        val user = userPreferences.getUser()
        val name = user?.name ?: "User"
        binding.tvWelcome.text = "Welcome, $name"
    }

    private fun setupListener() = with(binding) {
        btnLogout.setOnClickListener {
            userPreferences.setLoggedIn(false)
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}