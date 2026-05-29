package com.example.bible_application.ui.chapters

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
import kotlinx.coroutines.launch

class ChaptersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BibleRepository

    private val sharedPrefs = application.getSharedPreferences("reading_progress", android.content.Context.MODE_PRIVATE)

    private val _firstVerse = MutableStateFlow<BibleVerseEntity?>(null)
    val firstVerse: StateFlow<BibleVerseEntity?> = _firstVerse.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
    }

    fun getLastReadChapter(bookId: Int): Int {
        return sharedPrefs.getInt("last_read_${bookId}", -1)
    }

    fun getReadChapters(bookId: Int): Set<Int> {
        val stringSet = sharedPrefs.getStringSet("read_chapters_${bookId}", emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
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

    fun loadFirstVerse(bookId: Int) {
        viewModelScope.launch {
            repository.getVerses(bookId, 1).collect { list ->
                if (list.isNotEmpty()) {
                    _firstVerse.value = list.first()
                }
            }
        }
    }

    fun toggleBookBookmark() {
        val verse = _firstVerse.value ?: return
        viewModelScope.launch {
            val newBookmarkStatus = !verse.isBookmarked
            repository.setBookmark(verse, newBookmarkStatus)
            _firstVerse.value = verse.copy(isBookmarked = newBookmarkStatus)
        }
    }

    fun getChaptersFlow(bookId: Int): Flow<List<Int>> = repository.getChaptersForBook(bookId)

    fun getBookTestament(bookId: Int): String {
        return if (bookId <= 39) "Old Testament" else "New Testament"
    }

    fun getBookSummary(bookName: String): String {
        return when (bookName.lowercase()) {
            "genesis" -> "First book of the Bible, covering creation and early patriarchs."
            "exodus" -> "Covers Israel's departure from Egypt and the giving of the Law."
            "leviticus" -> "Detailed guide for temple sacrifices, laws, and ritual holiness."
            "numbers" -> "The journey of Israel through the wilderness towards the Promised Land."
            "deuteronomy" -> "Moses' final speeches repeating the Law to the new generation."
            "joshua" -> "The conquest and partition of the Promised Land under Joshua."
            "judges" -> "History of Israel's cycles of rebellion, oppression, and judges."
            "ruth" -> "A beautiful story of loyalty, redemption, and King David's lineage."
            "matthew" -> "Gospel emphasizing Jesus as King of Israel and Messiah."
            "mark" -> "Fast-paced Gospel presenting Jesus as the suffering Servant."
            "luke" -> "Detailed Gospel presenting Jesus as the Savior of all humanity."
            "john" -> "Fourth Gospel presenting Jesus as the divine Word made flesh."
            "acts" -> "History of the early Church and expansion of the Gospel."
            "romans" -> "Theological masterpiece explaining salvation by faith in Christ."
            "revelation" -> "The final prophetic vision of the victory of God's Kingdom."
            else -> "A book of the Holy Bible containing inspired scriptures."
        }
    }
}
