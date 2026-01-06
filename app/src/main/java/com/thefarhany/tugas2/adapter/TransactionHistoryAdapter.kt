package com.thefarhany.tugas2.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thefarhany.tugas2.R
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import com.thefarhany.tugas2.databinding.ItemTransactionHistoryBinding

class TransactionHistoryAdapter(
    private var items: List<TransactionResponse> = emptyList()
) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    fun submitList(newItems: List<TransactionResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(private val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TransactionResponse) = with(binding) {
            tvTypeAmount.text = "${item.type} • Rp ${item.amount}"
            tvDesc.text = item.description ?: "-"
            tvMeta.text = "${item.status} • ${item.createdAt ?: "-"}"

            val bgDrawable = when (item.type?.uppercase()) {
                "TOP_UP", "TOPUP" -> R.drawable.bg_transaction_topup
                "TRANSFER" -> R.drawable.bg_transaction_transfer
                "REQUEST", "MONEY_REQUEST" -> R.drawable.bg_transaction_request
                else -> R.drawable.bg_transaction_default
            }
            cardItem.setBackgroundResource(bgDrawable)
        }
    }
}
