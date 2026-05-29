package com.example.bible_application.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import com.example.bible_application.data.model.BibleVerseEntity

class SearchAdapter(
    private var results: List<BibleVerseEntity>,
    private val onItemClick: (BibleVerseEntity) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val referenceTextView: TextView = view.findViewById(R.id.verse_number) // Can repurpose for "Gen 1:1" reference style!
        val contentTextView: TextView = view.findViewById(R.id.verse_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verse = results[position]
        holder.referenceTextView.text = "${verse.bookName} ${verse.chapter}:${verse.verseNumber}"
        holder.contentTextView.text = verse.text
        holder.itemView.setOnClickListener { onItemClick(verse) }
    }

    override fun getItemCount() = results.size

    fun updateResults(newResults: List<BibleVerseEntity>) {
        results = newResults
        notifyDataSetChanged()
    }
}
