package com.example.bible_application.ui.verses

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import com.example.bible_application.data.model.BibleVerseEntity

class VersesAdapter(
    private var verses: List<BibleVerseEntity>,
    private var selectedVerseId: Int = -1,
    private var textSizeSp: Float = 17f,
    private var fontStyle: String = "Serif",
    private var lineSpacing: Float = 1.3f,
    private var theme: String = "Light",
    private val onItemClick: (BibleVerseEntity) -> Unit
) : RecyclerView.Adapter<VersesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberTextView: TextView = view.findViewById(R.id.verse_number)
        val contentTextView: TextView = view.findViewById(R.id.verse_text)
        val container: View = view.findViewById(R.id.verse_item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verse = verses[position]
        holder.numberTextView.text = verse.verseNumber.toString()
        holder.contentTextView.text = verse.text

        // 1. Dynamic Text Size
        holder.contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
        holder.numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.max(10f, textSizeSp - 4f))

        // 2. Dynamic Font Style
        if (fontStyle == "Serif") {
            holder.contentTextView.typeface = Typeface.SERIF
            holder.numberTextView.typeface = Typeface.SERIF
        } else {
            holder.contentTextView.typeface = Typeface.SANS_SERIF
            holder.numberTextView.typeface = Typeface.SANS_SERIF
        }

        // 3. Dynamic Line Spacing
        holder.contentTextView.setLineSpacing(0f, lineSpacing)

        // 4. Dynamic Theme Styling
        val textColor: Int
        val normalBgColor: Int
        val highlightBgColor: Int

        when (theme) {
            "Dark" -> {
                textColor = Color.parseColor("#E0E0E0")
                normalBgColor = Color.parseColor("#121212")
                highlightBgColor = Color.parseColor("#372F15") // Soft dark gold highlight
                holder.numberTextView.setTextColor(Color.parseColor("#80CBC4"))
            }
            "Sepia" -> {
                textColor = Color.parseColor("#5B4636")
                normalBgColor = Color.parseColor("#F4ECD8") // Traditional warm sepia cream color
                highlightBgColor = Color.parseColor("#EEDCA5") // Warm golden sepia highlight
                holder.numberTextView.setTextColor(Color.parseColor("#795548"))
            }
            else -> { // Light Theme
                textColor = Color.parseColor("#1E1E1E")
                normalBgColor = Color.TRANSPARENT
                highlightBgColor = Color.parseColor("#FFF9C4") // Light yellow highlight
                holder.numberTextView.setTextColor(Color.parseColor("#3F51B5"))
            }
        }

        holder.contentTextView.setTextColor(textColor)

        if (verse.highlightColor != null) {
            holder.container.setBackgroundColor(highlightBgColor)
        } else {
            holder.container.setBackgroundColor(normalBgColor)
        }

        holder.itemView.setOnClickListener {
            onItemClick(verse)
        }
    }

    override fun getItemCount() = verses.size

    fun updateVerses(newVerses: List<BibleVerseEntity>) {
        verses = newVerses
        notifyDataSetChanged()
    }

    fun updateDisplaySettings(
        newSizeSp: Float,
        newStyle: String,
        newSpacing: Float,
        newTheme: String
    ) {
        textSizeSp = newSizeSp
        fontStyle = newStyle
        lineSpacing = newSpacing
        theme = newTheme
        notifyDataSetChanged()
    }
}
