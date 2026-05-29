package com.example.bible_application.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val verseId: Int,
    val color: String, // Hex color string
    val highlightedAt: Long = System.currentTimeMillis()
)
