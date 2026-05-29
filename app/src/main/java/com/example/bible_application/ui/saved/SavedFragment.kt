package com.example.bible_application.ui.saved

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import com.example.bible_application.data.model.BibleVerseEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private lateinit var viewModel: SavedViewModel
    private lateinit var adapter: SavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SavedViewModel::class.java]

        val backButton = view.findViewById<ImageButton>(R.id.btn_back_saved)
        val appbarSearchButton = view.findViewById<ImageButton>(R.id.btn_appbar_search)
        val searchEditText = view.findViewById<EditText>(R.id.search_bookmarks_edit_text)
        
        val tabAll = view.findViewById<TextView>(R.id.tab_saved_all)
        val tabHighlighted = view.findViewById<TextView>(R.id.tab_saved_highlighted)
        val tabFavorites = view.findViewById<TextView>(R.id.tab_saved_favorites)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.saved_verses_recycler_view)
        val fabAddBookmark = view.findViewById<ImageButton>(R.id.fab_add_bookmark)

        // 1. Setup RecyclerView & SavedAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SavedAdapter(
            results = emptyList(),
            onItemClick = { verse ->
                // Navigate directly to Verses Fragment showing that chapter
                val bundle = Bundle().apply {
                    putInt("bookId", verse.bookId)
                    putString("bookName", verse.bookName)
                    putInt("chapter", verse.chapter)
                }
                findNavController().navigate(R.id.navigation_verses, bundle)
            },
            onOptionsClick = { anchorView, verse ->
                showOptionsMenu(anchorView, verse)
            }
        )
        recyclerView.adapter = adapter

        // 2. Setup Back Button Navigation
        backButton.setOnClickListener {
            // Standard back navigation
            findNavController().navigateUp()
        }

        // 3. Setup AppBar Search Focus
        appbarSearchButton.setOnClickListener {
            searchEditText.requestFocus()
            // Toast search helper
            Toast.makeText(context, "Search active", Toast.LENGTH_SHORT).show()
        }

        // 4. Setup Search Text changed listener
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 5. Setup Filter Tabs click listeners
        tabAll.setOnClickListener { viewModel.setActiveTab(SavedFilter.ALL) }
        tabHighlighted.setOnClickListener { viewModel.setActiveTab(SavedFilter.HIGHLIGHTED) }
        tabFavorites.setOnClickListener { viewModel.setActiveTab(SavedFilter.FAVORITES) }

        // 6. Setup Floating Action Button (FAB) navigation to books page
        fabAddBookmark.setOnClickListener {
            findNavController().navigate(R.id.navigation_books)
        }

        // 7. Observe state flows from SavedViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredSavedVerses.collect { list ->
                adapter.updateResults(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeTab.collect { filter ->
                updateTabsUi(tabAll, tabHighlighted, tabFavorites, filter)
            }
        }
    }

    private fun updateTabsUi(
        tabAll: TextView,
        tabHighlighted: TextView,
        tabFavorites: TextView,
        activeFilter: SavedFilter
    ) {
        val context = requireContext()
        val activeBg = ContextCompat.getDrawable(context, R.drawable.bg_chapter_active)
        val inactiveBg = ContextCompat.getDrawable(context, R.drawable.bg_chapter_normal)
        val activeColor = android.graphics.Color.parseColor("#1A56DB")
        val inactiveColor = android.graphics.Color.parseColor("#616161")

        tabAll.background = if (activeFilter == SavedFilter.ALL) activeBg else inactiveBg
        tabAll.setTextColor(if (activeFilter == SavedFilter.ALL) activeColor else inactiveColor)

        tabHighlighted.background = if (activeFilter == SavedFilter.HIGHLIGHTED) activeBg else inactiveBg
        tabHighlighted.setTextColor(if (activeFilter == SavedFilter.HIGHLIGHTED) activeColor else inactiveColor)

        tabFavorites.background = if (activeFilter == SavedFilter.FAVORITES) activeBg else inactiveBg
        tabFavorites.setTextColor(if (activeFilter == SavedFilter.FAVORITES) activeColor else inactiveColor)
    }

    private fun showOptionsMenu(anchorView: View, verse: BibleVerseEntity) {
        val context = requireContext()
        val popup = PopupMenu(context, anchorView)
        
        popup.menu.add("Share Verse")
        if (verse.isBookmarked) {
            popup.menu.add("Remove Bookmark")
        }
        if (verse.highlightColor != null) {
            popup.menu.add("Clear Highlight")
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Share Verse" -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "\"${verse.text}\" — ${verse.bookName} ${verse.chapter}:${verse.verseNumber}")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share verse via"))
                }
                "Remove Bookmark" -> {
                    viewModel.deleteBookmark(verse)
                    Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                }
                "Clear Highlight" -> {
                    viewModel.clearHighlight(verse)
                    Toast.makeText(context, "Highlight cleared", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }
}
