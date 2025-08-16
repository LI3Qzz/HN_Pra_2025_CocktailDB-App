package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.databinding.ItemSearchHistoryBinding

class HistoryAdapter(
    private val onHistoryClicked: (String) -> Unit,
    private val onHistoryRemoved: (String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyItems: List<String> = emptyList()

    fun updateHistory(newHistory: List<String>) {
        historyItems = newHistory
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount(): Int = historyItems.size

    inner class HistoryViewHolder(
        private val binding: ItemSearchHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHistoryClicked(historyItems[position])
                }
            }

            binding.btnRemoveHistory.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHistoryRemoved(historyItems[position])
                }
            }
        }

        fun bind(historyItem: String) {
            binding.tvHistoryText.text = historyItem
        }
    }
}
