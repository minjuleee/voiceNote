package com.example.voicenote.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenote.R

class MemoAdapter(
    private var originalList: List<Memo>,
    private val onItemClick: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private var filteredList: List<Memo> = originalList.toList()

    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textSummary: TextView = itemView.findViewById(R.id.textSummary)
        val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = filteredList[position]
        holder.textTitle.text = memo.title
        holder.textSummary.text = memo.summary
        holder.textDateTime.text = memo.dateTime

        holder.itemView.setOnClickListener { onItemClick(memo) }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Memo>) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
