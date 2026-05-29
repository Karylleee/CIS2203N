package com.example.bible_application.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class BibleVerseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String,
    val isBookmarked: Boolean = false,
    val highlightColor: String? = null // Hex color code or null if not highlighted
)
