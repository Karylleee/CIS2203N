package com.example.bible_application.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R

class RecentSearchAdapter(
    private var items: List<String>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val queryTextView: TextView = view.findViewById(R.id.recent_search_text)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_clear_recent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val query = items[position]
        holder.queryTextView.text = query
        holder.itemView.setOnClickListener { onItemClick(query) }
        holder.deleteButton.setOnClickListener { onDeleteClick(query) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}
