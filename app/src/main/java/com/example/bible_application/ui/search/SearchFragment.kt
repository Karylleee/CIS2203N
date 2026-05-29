package com.example.bible_application.ui.search

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recentAdapter: RecentSearchAdapter

    companion object {
        private const val VOICE_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val searchHomeContainer = view.findViewById<View>(R.id.search_home_container)
        val searchResultsRecyclerView = view.findViewById<RecyclerView>(R.id.search_results_recycler_view)
        val btnVoiceSearch = view.findViewById<ImageButton>(R.id.btn_voice_search)

        val tabVerses = view.findViewById<TextView>(R.id.tab_search_verses)
        val tabKeywords = view.findViewById<TextView>(R.id.tab_search_keywords)
        val tabTopics = view.findViewById<TextView>(R.id.tab_search_topics)

        // 1. Setup Search Results Adapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(emptyList()) { verse ->
            // Save search to history when item clicked
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.addRecentSearch(query)
            }
            
            // Navigate to VersesFragment
            val bundle = Bundle().apply {
                putInt("bookId", verse.bookId)
                putString("bookName", verse.bookName)
                putInt("chapter", verse.chapter)
            }
            findNavController().navigate(R.id.navigation_verses, bundle)
        }
        searchResultsRecyclerView.adapter = searchAdapter

        // 2. Setup Recent Searches Adapter
        val recentRecyclerView = view.findViewById<RecyclerView>(R.id.recent_searches_recycler_view)
        recentRecyclerView.layoutManager = LinearLayoutManager(context)
        recentAdapter = RecentSearchAdapter(
            items = emptyList(),
            onItemClick = { query ->
                searchEditText.setText(query)
                searchEditText.setSelection(query.length)
                viewModel.setSearchQuery(query)
                viewModel.addRecentSearch(query)
            },
            onDeleteClick = { query ->
                viewModel.removeRecentSearch(query)
            }
        )
        recentRecyclerView.adapter = recentAdapter

        // 3. Handle Text input & visibility toggle
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                viewModel.setSearchQuery(query)
                if (query.trim().isEmpty()) {
                    searchHomeContainer.visibility = View.VISIBLE
                    searchResultsRecyclerView.visibility = View.GONE
                } else {
                    searchHomeContainer.visibility = View.GONE
                    searchResultsRecyclerView.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Add to history on IME Search press
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.addRecentSearch(query)
                }
                true
            } else {
                false
            }
        }

        // 4. Handle Voice input
        btnVoiceSearch.setOnClickListener {
            startVoiceInput()
        }

        // 5. Handle Scope/Filter Tabs
        tabVerses.setOnClickListener { viewModel.setActiveTab(SearchTab.VERSES) }
        tabKeywords.setOnClickListener { viewModel.setActiveTab(SearchTab.KEYWORDS) }
        tabTopics.setOnClickListener { viewModel.setActiveTab(SearchTab.TOPICS) }

        // 6. Handle Topics Chips Click
        val chips = listOf(
            view.findViewById<TextView>(R.id.chip_faith),
            view.findViewById<TextView>(R.id.chip_hope),
            view.findViewById<TextView>(R.id.chip_love),
            view.findViewById<TextView>(R.id.chip_peace),
            view.findViewById<TextView>(R.id.chip_strength),
            view.findViewById<TextView>(R.id.chip_grace),
            view.findViewById<TextView>(R.id.chip_forgiveness),
            view.findViewById<TextView>(R.id.chip_wisdom)
        )
        chips.forEach { chip ->
            chip.setOnClickListener {
                val text = chip.text.toString()
                searchEditText.setText(text)
                searchEditText.setSelection(text.length)
                viewModel.setSearchQuery(text)
                viewModel.addRecentSearch(text)
            }
        }

        // 7. Observe state from SearchViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResultsFlow.collect { list ->
                searchAdapter.updateResults(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentSearches.collect { list ->
                recentAdapter.updateItems(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeTab.collect { activeTab ->
                updateTabUi(tabVerses, tabKeywords, tabTopics, activeTab)
            }
        }
    }

    private fun updateTabUi(
        tabVerses: TextView,
        tabKeywords: TextView,
        tabTopics: TextView,
        activeTab: SearchTab
    ) {
        val activeBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chapter_active)
        val normalBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chapter_normal)
        val activeColor = Color.parseColor("#1A56DB")
        val normalColor = Color.parseColor("#616161")

        tabVerses.background = if (activeTab == SearchTab.VERSES) activeBg else normalBg
        tabVerses.setTextColor(if (activeTab == SearchTab.VERSES) activeColor else normalColor)

        tabKeywords.background = if (activeTab == SearchTab.KEYWORDS) activeBg else normalBg
        tabKeywords.setTextColor(if (activeTab == SearchTab.KEYWORDS) activeColor else normalColor)

        tabTopics.background = if (activeTab == SearchTab.TOPICS) activeBg else normalBg
        tabTopics.setTextColor(if (activeTab == SearchTab.TOPICS) activeColor else normalColor)
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak reference or keyword...")
        }
        try {
            startActivityForResult(intent, VOICE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice search is not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0) ?: ""
            if (spokenText.isNotEmpty()) {
                val searchEditText = view?.findViewById<EditText>(R.id.search_edit_text)
                searchEditText?.setText(spokenText)
                searchEditText?.setSelection(spokenText.length)
                viewModel.setSearchQuery(spokenText)
                viewModel.addRecentSearch(spokenText)
            }
        }
    }
}
