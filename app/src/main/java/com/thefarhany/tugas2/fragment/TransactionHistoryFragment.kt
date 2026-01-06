package com.thefarhany.tugas2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.data.viewmodel.TransactionHistoryViewModel
import com.thefarhany.tugas2.databinding.FragmentTransactionHistoryBinding
import com.thefarhany.tugas2.ui.history.TransactionHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionHistoryFragment : Fragment() {

    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private val adapter = TransactionHistoryAdapter()

    private val viewModel: TransactionHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        observeViewModel()

        // Trigger fetch
        viewModel.loadHistory(cookieHeader())
    }

    private fun observeViewModel() {
        // Observe history list
        viewModel.history.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe loading
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cookieHeader(): String {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) throw IllegalStateException("Token kosong. Silakan login ulang.")
        return "jwt=$token"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
