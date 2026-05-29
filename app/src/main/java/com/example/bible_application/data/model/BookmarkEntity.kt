package com.example.bible_application.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val verseId: Int,
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
