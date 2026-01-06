package com.thefarhany.tugas2.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thefarhany.tugas2.R
import com.thefarhany.tugas2.adapter.PendingRequestAdapter
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.databinding.ActivityHomeBinding
import com.thefarhany.tugas2.fragment.ProfileFragment
import com.thefarhany.tugas2.fragment.TransactionFragment
import com.thefarhany.tugas2.fragment.TransactionHistoryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = PendingRequestAdapter(
        onApprove = { request -> viewModel.approveRequest(cookieHeader(), request.id) },
        onReject = { request -> viewModel.rejectRequest(cookieHeader(), request.id) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerView()
        observeViewModel()
        showHomeContent()

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> {
                    showHomeContent()
                    true
                }
                R.id.itemTransaction -> {
                    showFragment(TransactionFragment())
                    true
                }
                R.id.itemHistory -> {
                    showFragment(TransactionHistoryFragment())
                    true
                }
                R.id.itemProfile -> {
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvPendingRequests.layoutManager = LinearLayoutManager(this)
        binding.rvPendingRequests.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.balance.observe(this) { balance ->
            binding.tvBalance.text = if (balance != null) "Rp ${balance.amount}" else "Rp -"
        }

        viewModel.pendingRequests.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmptyRequests.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressRequests.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.successMessage.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cookieHeader(): String {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) throw IllegalStateException("Token kosong")
        return "jwt=$token"
    }

    private fun showHomeContent() {
        binding.homeContent.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE
        supportFragmentManager.popBackStack()

        viewModel.loadBalance(cookieHeader())
        viewModel.loadPendingRequests(cookieHeader())
    }

    private fun showFragment(fragment: Fragment) {
        binding.homeContent.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
