package com.thefarhany.tugas2.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.data.viewmodel.TransactionViewModel
import com.thefarhany.tugas2.databinding.FragmentTransactionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        binding.btnTopUp.setOnClickListener { showTopUpDialog() }
        binding.btnTransfer.setOnClickListener { showTransferDialog() }
        binding.btnRequest.setOnClickListener { showRequestDialog() }

        observeViewModel()
        viewModel.loadBalance(cookieHeader())
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = if (balance != null) "Rp ${balance.amount}" else "Rp -"
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Opsional: bisa tampilin ProgressBar kalau mau
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun showTopUpDialog() {
        val etAmount = EditText(requireContext()).apply { hint = "Amount (contoh: 50000)" }
        val etDesc = EditText(requireContext()).apply { hint = "Description (opsional)" }
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(etAmount)
            addView(etDesc)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Top Up")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val amount = etAmount.text.toString().trim().toDoubleOrNull()
                val desc = etDesc.text.toString().trim().ifBlank { null }
                if (amount == null || amount <= 0) {
                    Toast.makeText(requireContext(), "Amount tidak valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.topUp(cookieHeader(), amount, desc)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTransferDialog() {
        val etReceiver = EditText(requireContext()).apply { hint = "Receiver username" }
        val etAmount = EditText(requireContext()).apply { hint = "Amount" }
        val etDesc = EditText(requireContext()).apply { hint = "Description (opsional)" }
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(etReceiver)
            addView(etAmount)
            addView(etDesc)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Transfer")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val receiver = etReceiver.text.toString().trim()
                val amount = etAmount.text.toString().trim().toDoubleOrNull()
                val desc = etDesc.text.toString().trim().ifBlank { null }

                if (receiver.isBlank()) {
                    Toast.makeText(requireContext(), "Receiver wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (amount == null || amount <= 0) {
                    Toast.makeText(requireContext(), "Amount tidak valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.transfer(cookieHeader(), receiver, amount, desc)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRequestDialog() {
        val etPayer = EditText(requireContext()).apply { hint = "Payer username (yang akan bayar)" }
        val etAmount = EditText(requireContext()).apply { hint = "Amount" }
        val etDesc = EditText(requireContext()).apply { hint = "Description (opsional)" }
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(etPayer)
            addView(etAmount)
            addView(etDesc)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Request Money")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val payer = etPayer.text.toString().trim()
                val amount = etAmount.text.toString().trim().toDoubleOrNull()
                val desc = etDesc.text.toString().trim().ifBlank { null }

                if (payer.isBlank()) {
                    Toast.makeText(requireContext(), "Payer wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (amount == null || amount <= 0) {
                    Toast.makeText(requireContext(), "Amount tidak valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.requestMoney(cookieHeader(), payer, amount, desc)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
