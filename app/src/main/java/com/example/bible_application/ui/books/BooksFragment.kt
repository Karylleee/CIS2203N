package com.example.bible_application.ui.books

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import kotlinx.coroutines.launch

class BooksFragment : Fragment() {

    private lateinit var viewModel: BooksViewModel
    private lateinit var adapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BooksViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.books_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Close button: navigates back to Home screen
        val btnClose = view.findViewById<ImageButton>(R.id.btn_close_books)
        btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // Segmented Control Tabs
        val tabTraditional = view.findViewById<TextView>(R.id.tab_traditional)
        val tabAlphabetical = view.findViewById<TextView>(R.id.tab_alphabetical)

        adapter = BooksAdapter(emptyList()) { book ->
            val bundle = Bundle().apply {
                putInt("bookId", book.bookId)
                putString("bookName", book.bookName)
            }
            findNavController().navigate(R.id.action_books_to_chapters, bundle)
        }
        recyclerView.adapter = adapter

        // Observe books list flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.booksFlow.collect { list ->
                adapter.updateBooks(list)
            }
        }

        // Tab click listeners for dynamic sorting and style switching
        tabTraditional.setOnClickListener {
            viewModel.setSortingMode("traditional")
            tabTraditional.setBackgroundResource(R.drawable.bg_segmented_active)
            tabTraditional.setTextColor(Color.parseColor("#1E1E1E"))
            tabTraditional.setTypeface(null, android.graphics.Typeface.BOLD)

            tabAlphabetical.setBackgroundColor(Color.TRANSPARENT)
            tabAlphabetical.setTextColor(Color.parseColor("#616161"))
            tabAlphabetical.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        tabAlphabetical.setOnClickListener {
            viewModel.setSortingMode("alphabetical")
            tabAlphabetical.setBackgroundResource(R.drawable.bg_segmented_active)
            tabAlphabetical.setTextColor(Color.parseColor("#1E1E1E"))
            tabAlphabetical.setTypeface(null, android.graphics.Typeface.BOLD)

            tabTraditional.setBackgroundColor(Color.TRANSPARENT)
            tabTraditional.setTextColor(Color.parseColor("#616161"))
            tabTraditional.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }
}
