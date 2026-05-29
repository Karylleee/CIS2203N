package com.example.bible_application.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.model.BibleVerseEntity
import com.example.bible_application.data.repository.BibleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BibleRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
        
        // Observe database initialization, and load daily verse as soon as data preloads!
        viewModelScope.launch {
            repository.getAllBooks().collect { books ->
                if (books.isNotEmpty()) {
                    loadDailyVerse()
                }
            }
        }
    }

    private val _dailyVerse = MutableStateFlow<BibleVerseEntity?>(null)
    val dailyVerse: StateFlow<BibleVerseEntity?> = _dailyVerse.asStateFlow()

    private fun loadDailyVerse() {
        viewModelScope.launch {
            val totalCount = repository.getVerseCount()
            if (totalCount > 0) {
                val calendar = Calendar.getInstance()
                val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)
                
                // Construct a stable day-changing index
                val absoluteDay = year * 365 + dayOfYear
                val dailyId = (absoluteDay % totalCount) + 1
                
                repository.getVerseById(dailyId).collect { verse ->
                    if (verse != null) {
                        _dailyVerse.value = verse
                    }
                }
            }
        }
    }

    fun toggleBookmark() {
        val verse = _dailyVerse.value ?: return
        viewModelScope.launch {
            val newBookmarkStatus = !verse.isBookmarked
            repository.setBookmark(verse, newBookmarkStatus)
            _dailyVerse.value = verse.copy(isBookmarked = newBookmarkStatus)
        }
    }
}
