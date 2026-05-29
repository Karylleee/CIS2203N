package com.example.bible_application.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bible_application.data.model.BibleVerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleVerseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVerses(verses: List<BibleVerseEntity>): List<Long>

    @Query("SELECT DISTINCT bookId, bookName FROM verses ORDER BY bookId ASC")
    fun getAllBooks(): Flow<List<BookInfo>>

    @Query("SELECT DISTINCT chapter FROM verses WHERE bookId = :bookId ORDER BY chapter ASC")
    fun getChaptersForBook(bookId: Int): Flow<List<Int>>

    @Query("SELECT * FROM verses WHERE bookId = :bookId AND chapter = :chapter ORDER BY verseNumber ASC")
    fun getVerses(bookId: Int, chapter: Int): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :query || '%'")
    fun searchVerses(query: String): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM verses WHERE bookName LIKE :bookNamePattern AND chapter = :chapter AND verseNumber = :verseNumber")
    fun getVerseByReference(bookNamePattern: String, chapter: Int, verseNumber: Int): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM verses WHERE bookName LIKE :bookNamePattern AND chapter = :chapter ORDER BY verseNumber ASC")
    fun getVersesByChapterReference(bookNamePattern: String, chapter: Int): Flow<List<BibleVerseEntity>>

    @Query("UPDATE verses SET isBookmarked = :isBookmarked WHERE id = :verseId")
    fun updateBookmarkStatus(verseId: Int, isBookmarked: Boolean): Int

    @Query("UPDATE verses SET highlightColor = :color WHERE id = :verseId")
    fun updateHighlightColor(verseId: Int, color: String?): Int

    @Query("SELECT COUNT(*) FROM verses")
    fun getVerseCount(): Int

    @Query("SELECT * FROM verses WHERE isBookmarked = 1 OR highlightColor IS NOT NULL")
    fun getSavedVerses(): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM verses WHERE id = :id")
    fun getVerseById(id: Int): Flow<BibleVerseEntity?>
}

data class BookInfo(
    val bookId: Int,
    val bookName: String
)
