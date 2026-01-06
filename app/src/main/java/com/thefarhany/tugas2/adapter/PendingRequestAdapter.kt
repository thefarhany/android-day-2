package com.thefarhany.tugas2.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import com.thefarhany.tugas2.databinding.ItemPendingRequestBinding

class PendingRequestAdapter(
    private var items: List<TransactionResponse> = emptyList(),
    private val onApprove: (TransactionResponse) -> Unit,
    private val onReject: (TransactionResponse) -> Unit
) : RecyclerView.Adapter<PendingRequestAdapter.ViewHolder>() {

    fun submitList(newItems: List<TransactionResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPendingRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemPendingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TransactionResponse) = with(binding) {
            tvRequestFrom.text = "From: ${item.senderUsername}"
            tvAmount.text = "Rp ${item.amount}"
            tvDescription.text = item.description ?: "-"
            tvMeta.text = "${item.status} • ${item.createdAt ?: "-"}"

            btnApprove.setOnClickListener { onApprove(item) }
            btnReject.setOnClickListener { onReject(item) }
        }
    }
}