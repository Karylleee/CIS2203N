package com.example.bible_application.ui.verses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.repository.BibleRepository
import com.example.bible_application.data.model.BibleVerseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VersesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BibleRepository

    private val sharedPrefs = application.getSharedPreferences("reading_progress", android.content.Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
    }

    fun markChapterAsRead(bookId: Int, chapter: Int) {
        val readSet = sharedPrefs.getStringSet("read_chapters_${bookId}", emptySet()) ?: emptySet()
        val newReadSet = readSet.toMutableSet()
        newReadSet.add(chapter.toString())
        
        sharedPrefs.edit()
            .putInt("last_read_${bookId}", chapter)
            .putStringSet("read_chapters_${bookId}", newReadSet)
            .apply()
    }

    fun toggleVerseHighlight(verse: BibleVerseEntity, highlightColor: String) {
        val newColor = if (verse.highlightColor != null) null else highlightColor
        viewModelScope.launch {
            repository.setHighlight(verse.id, newColor)
        }
    }

    fun getVersesFlow(bookId: Int, chapter: Int): Flow<List<BibleVerseEntity>> =
        repository.getVerses(bookId, chapter)

    fun getChaptersCountFlow(bookId: Int): Flow<List<Int>> =
        repository.getChaptersForBook(bookId)

    fun getBookTestament(bookId: Int): String {
        return if (bookId <= 39) "Old Testament" else "New Testament"
    }
}
