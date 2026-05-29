package com.example.bible_application.ui.books

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.repository.BibleRepository
import com.example.bible_application.data.local.BookInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BooksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BibleRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
    }

    private val _sortingMode = MutableStateFlow("traditional")
    val sortingMode: Flow<String> = _sortingMode

    val booksFlow: Flow<List<BookInfo>> = combine(
        repository.getAllBooks(),
        _sortingMode
    ) { books, mode ->
        if (mode == "alphabetical") {
            books.sortedBy { it.bookName }
        } else {
            books.sortedBy { it.bookId }
        }
    }

    fun setSortingMode(mode: String) {
        _sortingMode.value = mode
    }
}
