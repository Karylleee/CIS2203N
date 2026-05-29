package com.example.bible_application.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.repository.BibleRepository
import com.example.bible_application.data.model.BibleVerseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

enum class SearchTab { VERSES, KEYWORDS, TOPICS }

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BibleRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeTab = MutableStateFlow(SearchTab.VERSES)
    val activeTab: StateFlow<SearchTab> = _activeTab.asStateFlow()

    private val _recentSearches = MutableStateFlow(listOf("John 3:16", "peace", "Psalm 23"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResultsFlow: Flow<List<BibleVerseEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.trim().isEmpty()) {
            flowOf(emptyList())
        } else {
            repository.searchVerses(query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveTab(tab: SearchTab) {
        _activeTab.value = tab
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(trimmed)
        currentList.add(0, trimmed)
        if (currentList.size > 10) {
            currentList.removeAt(currentList.size - 1)
        }
        _recentSearches.value = currentList
    }

    fun removeRecentSearch(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query.trim())
        _recentSearches.value = currentList
    }
}
