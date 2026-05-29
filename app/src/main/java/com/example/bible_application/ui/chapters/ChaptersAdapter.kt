package com.example.bible_application.ui.chapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R

class ChaptersAdapter(
    private var chapters: List<Int>,
    private var readChapters: Set<Int> = emptySet(),
    private var lastReadChapter: Int = -1,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ChaptersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterNumberTextView: TextView = view.findViewById(R.id.chapter_number)
        val container: View = view.findViewById(R.id.chapter_item_container)
        val activeDot: View = view.findViewById(R.id.chapter_active_dot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.chapterNumberTextView.text = chapter.toString()

        if (chapter == lastReadChapter) {
            // Last read chapter: active blue background tint and outline + visible blue dot
            holder.container.setBackgroundResource(R.drawable.bg_chapter_active)
            holder.activeDot.visibility = View.VISIBLE
            holder.chapterNumberTextView.setTextColor(holder.itemView.context.getColor(android.R.color.black))
        } else {
            // Normal chapter: no active dot
            holder.container.setBackgroundResource(R.drawable.bg_chapter_normal)
            holder.activeDot.visibility = View.GONE
            
            if (readChapters.contains(chapter)) {
                // Read chapter: dark text
                holder.chapterNumberTextView.setTextColor(android.graphics.Color.parseColor("#1E1E1E"))
            } else {
                // Unread chapter: lighter grey text
                holder.chapterNumberTextView.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(chapter)
        }
    }

    override fun getItemCount() = chapters.size

    fun updateChapters(newChapters: List<Int>, newReadChapters: Set<Int>, newLastRead: Int) {
        chapters = newChapters
        readChapters = newReadChapters
        lastReadChapter = newLastRead
        notifyDataSetChanged()
    }
}
