package com.example.bible_application.ui.books

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import com.example.bible_application.data.local.BookInfo

class BooksAdapter(
    private var books: List<BookInfo>,
    private var selectedBookId: Int = -1,
    private val onItemClick: (BookInfo) -> Unit
) : RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookNameTextView: TextView = view.findViewById(R.id.book_name)
        val container: View = view.findViewById(R.id.book_item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.bookNameTextView.text = book.bookName

        // Highlight selected book with soft purple background
        if (book.bookId == selectedBookId) {
            holder.container.setBackgroundColor(Color.parseColor("#F3E8FF"))
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            // Update selected ID locally and notify refresh
            selectedBookId = book.bookId
            notifyDataSetChanged()
            onItemClick(book)
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<BookInfo>, newSelectedId: Int = -1) {
        books = newBooks
        selectedBookId = newSelectedId
        notifyDataSetChanged()
    }
}
