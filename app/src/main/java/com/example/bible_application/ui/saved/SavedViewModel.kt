package com.example.bible_application.ui.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.model.BibleVerseEntity
import com.example.bible_application.data.repository.BibleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class SavedFilter { ALL, HIGHLIGHTED, FAVORITES }

class SavedViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _activeTab = MutableStateFlow(SavedFilter.ALL)
    val activeTab: StateFlow<SavedFilter> = _activeTab.asStateFlow()

    val filteredSavedVerses: Flow<List<BibleVerseEntity>> = combine(
        repository.getSavedVerses(),
        _searchQuery,
        _activeTab
    ) { list, query, tab ->
        list.filter { verse ->
            val matchesTab = when (tab) {
                SavedFilter.ALL -> true
                SavedFilter.HIGHLIGHTED -> verse.highlightColor != null
                SavedFilter.FAVORITES -> verse.isBookmarked
            }
            val matchesQuery = if (query.trim().isEmpty()) {
                true
            } else {
                verse.bookName.contains(query, ignoreCase = true) ||
                        verse.text.contains(query, ignoreCase = true)
            }
            matchesTab && matchesQuery
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveTab(tab: SavedFilter) {
        _activeTab.value = tab
    }

    fun deleteBookmark(verse: BibleVerseEntity) {
        viewModelScope.launch {
            repository.setBookmark(verse, false)
        }
    }

    fun clearHighlight(verse: BibleVerseEntity) {
        viewModelScope.launch {
            repository.setHighlight(verse.id, null)
        }
    }
}
