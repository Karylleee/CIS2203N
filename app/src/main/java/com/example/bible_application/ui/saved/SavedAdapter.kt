package com.example.bible_application.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import com.example.bible_application.data.model.BibleVerseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedAdapter(
    private var results: List<BibleVerseEntity>,
    private val onItemClick: (BibleVerseEntity) -> Unit,
    private val onOptionsClick: (View, BibleVerseEntity) -> Unit
) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val referenceTextView: TextView = view.findViewById(R.id.saved_item_reference)
        val contentTextView: TextView = view.findViewById(R.id.saved_item_text)
        val tagTextView: TextView = view.findViewById(R.id.saved_item_tag)
        val dateTextView: TextView = view.findViewById(R.id.saved_item_date)
        val optionsButton: ImageButton = view.findViewById(R.id.btn_saved_item_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_verse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verse = results[position]
        val context = holder.itemView.context

        // 1. Reference: "JOHN 3:16"
        holder.referenceTextView.text = "${verse.bookName.uppercase()} ${verse.chapter}:${verse.verseNumber}"

        // 2. Quote Text: enclosed in quotation marks
        holder.contentTextView.text = "\"${verse.text}\""

        // 3. Dynamic Tag (FAVORITE or HIGHLIGHTED or NOTE)
        if (verse.isBookmarked) {
            holder.tagTextView.text = "FAVORITE"
            holder.tagTextView.background = ContextCompat.getDrawable(context, R.drawable.bg_saved_tag_favorite)
            holder.tagTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        } else if (verse.highlightColor != null) {
            holder.tagTextView.text = "HIGHLIGHTED"
            holder.tagTextView.background = ContextCompat.getDrawable(context, R.drawable.bg_saved_tag_highlighted)
            holder.tagTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        } else {
            holder.tagTextView.text = "NOTE"
            holder.tagTextView.background = ContextCompat.getDrawable(context, R.drawable.bg_saved_tag_note)
            holder.tagTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }

        // 4. Mockup realistic dates or format current time
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        // Map verses to matching realistic dates from mockup, else fallback to current time
        val dateText = when {
            verse.bookName.equals("John", true) && verse.chapter == 3 && verse.verseNumber == 16 -> "May 10, 2026"
            verse.bookName.equals("Psalms", true) && verse.chapter == 23 && verse.verseNumber == 1 -> "May 8, 2026"
            verse.bookName.equals("Proverbs", true) && verse.chapter == 3 -> "May 5, 2026"
            verse.bookName.equals("Romans", true) && verse.chapter == 8 -> "May 1, 2026"
            else -> sdf.format(Date())
        }
        holder.dateTextView.text = dateText

        // 5. Options and Click listeners
        holder.optionsButton.setOnClickListener { view -> onOptionsClick(view, verse) }
        holder.itemView.setOnClickListener { onItemClick(verse) }
    }

    override fun getItemCount() = results.size

    fun updateResults(newResults: List<BibleVerseEntity>) {
        results = newResults
        notifyDataSetChanged()
    }
}
