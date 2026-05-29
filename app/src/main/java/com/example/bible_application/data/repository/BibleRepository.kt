package com.example.bible_application.data.repository

import com.example.bible_application.data.local.BibleVerseDao
import com.example.bible_application.data.local.BookmarkDao
import com.example.bible_application.data.local.HighlightDao
import com.example.bible_application.data.local.BookInfo
import com.example.bible_application.data.model.BibleVerseEntity
import com.example.bible_application.data.model.BookmarkEntity
import com.example.bible_application.data.model.HighlightEntity
import kotlinx.coroutines.flow.Flow

class BibleRepository(
    private val bibleVerseDao: BibleVerseDao,
    private val bookmarkDao: BookmarkDao,
    private val highlightDao: HighlightDao
) {
    fun getAllBooks(): Flow<List<BookInfo>> = bibleVerseDao.getAllBooks()

    fun getChaptersForBook(bookId: Int): Flow<List<Int>> = bibleVerseDao.getChaptersForBook(bookId)

    fun getVerses(bookId: Int, chapter: Int): Flow<List<BibleVerseEntity>> = bibleVerseDao.getVerses(bookId, chapter)

    private val fullRefRegex = Regex("""^([1-3]?\s*[A-Za-z]+)\s+(\d+)[:\s]+(\d+)$""")
    private val chapterRefRegex = Regex("""^([1-3]?\s*[A-Za-z]+)\s+(\d+)$""")

    fun searchVerses(query: String): Flow<List<BibleVerseEntity>> {
        val trimmed = query.trim()
        val fullMatch = fullRefRegex.matchEntire(trimmed)
        if (fullMatch != null) {
            val book = fullMatch.groupValues[1]
            val chapter = fullMatch.groupValues[2].toIntOrNull() ?: 1
            val verse = fullMatch.groupValues[3].toIntOrNull() ?: 1
            return bibleVerseDao.getVerseByReference("$book%", chapter, verse)
        }

        val chapterMatch = chapterRefRegex.matchEntire(trimmed)
        if (chapterMatch != null) {
            val book = chapterMatch.groupValues[1]
            val chapter = chapterMatch.groupValues[2].toIntOrNull() ?: 1
            return bibleVerseDao.getVersesByChapterReference("$book%", chapter)
        }

        return bibleVerseDao.searchVerses(trimmed)
    }

    suspend fun setBookmark(verse: BibleVerseEntity, isBookmarked: Boolean) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        bibleVerseDao.updateBookmarkStatus(verse.id, isBookmarked)
        if (isBookmarked) {
            bookmarkDao.addBookmark(
                BookmarkEntity(
                    verseId = verse.id,
                    bookName = verse.bookName,
                    chapter = verse.chapter,
                    verseNumber = verse.verseNumber
                )
            )
        } else {
            bookmarkDao.removeBookmarkByVerseId(verse.id)
        }
    }

    suspend fun setHighlight(verseId: Int, color: String?) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        bibleVerseDao.updateHighlightColor(verseId, color)
        if (color != null) {
            highlightDao.addHighlight(
                HighlightEntity(
                    verseId = verseId,
                    color = color
                )
            )
        } else {
            highlightDao.removeHighlightByVerseId(verseId)
        }
    }

    fun getSavedVerses(): Flow<List<BibleVerseEntity>> = bibleVerseDao.getSavedVerses()

    fun getVerseById(id: Int): Flow<BibleVerseEntity?> = bibleVerseDao.getVerseById(id)

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun getAllHighlights(): Flow<List<HighlightEntity>> = highlightDao.getAllHighlights()

    suspend fun getVerseCount(): Int = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        bibleVerseDao.getVerseCount()
    }

    suspend fun isDatabaseEmpty(): Boolean {
        return bibleVerseDao.getVerseCount() == 0
    }

    suspend fun insertVerses(verses: List<BibleVerseEntity>) {
        bibleVerseDao.insertVerses(verses)
    }
}
