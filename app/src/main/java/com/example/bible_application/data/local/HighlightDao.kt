package com.example.bible_application.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bible_application.data.model.HighlightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHighlight(highlight: HighlightEntity): Long

    @Query("DELETE FROM highlights WHERE verseId = :verseId")
    fun removeHighlightByVerseId(verseId: Int): Int

    @Query("SELECT * FROM highlights ORDER BY highlightedAt DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>
}
