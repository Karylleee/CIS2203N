package com.example.bible_application.data.importer

import android.content.Context
import com.example.bible_application.data.model.BibleVerseEntity
import com.example.bible_application.data.repository.BibleRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BibleJsonImporter(
    private val context: Context,
    private val repository: BibleRepository
) {
    suspend fun importIfNeeded() = withContext(Dispatchers.IO) {
        if (repository.isDatabaseEmpty()) {
            val jsonString = context.assets.open("bible.json").bufferedReader().use { it.readText() }
            val bookType = object : TypeToken<List<JsonBook>>() {}.type
            val jsonBooks: List<JsonBook> = Gson().fromJson(jsonString, bookType)

            val entities = mutableListOf<BibleVerseEntity>()
            jsonBooks.forEachIndexed { bookIndex, jsonBook ->
                val bookId = bookIndex + 1
                val bookName = getBookName(jsonBook.abbrev)
                jsonBook.chapters.forEachIndexed { chapterIndex, versesList ->
                    val chapter = chapterIndex + 1
                    versesList.forEachIndexed { verseIndex, verseText ->
                        val verseNumber = verseIndex + 1
                        entities.add(
                            BibleVerseEntity(
                                bookId = bookId,
                                bookName = bookName,
                                chapter = chapter,
                                verseNumber = verseNumber,
                                text = verseText
                            )
                        )
                    }
                }
            }
            repository.insertVerses(entities)
        }
    }

    private data class JsonBook(
        val abbrev: String,
        val chapters: List<List<String>>
    )

    private fun getBookName(abbrev: String): String {
        return when (abbrev.lowercase()) {
            "gn" -> "Genesis"
            "ex" -> "Exodus"
            "lv" -> "Leviticus"
            "nm" -> "Numbers"
            "dt" -> "Deuteronomy"
            "js" -> "Joshua"
            "jud" -> "Judges"
            "rt" -> "Ruth"
            "1sm" -> "1 Samuel"
            "2sm" -> "2 Samuel"
            "1kgs" -> "1 Kings"
            "2kgs" -> "2 Kings"
            "1ch" -> "1 Chronicles"
            "2ch" -> "2 Chronicles"
            "ezr" -> "Ezra"
            "ne" -> "Nehemiah"
            "et" -> "Esther"
            "job" -> "Job"
            "ps" -> "Psalms"
            "prv" -> "Proverbs"
            "ec" -> "Ecclesiastes"
            "so" -> "Song of Solomon"
            "is" -> "Isaiah"
            "jr" -> "Jeremiah"
            "lm" -> "Lamentations"
            "ez" -> "Ezekiel"
            "dn" -> "Daniel"
            "ho" -> "Hosea"
            "jl" -> "Joel"
            "am" -> "Amos"
            "ob" -> "Obadiah"
            "jn" -> "Jonah"
            "mi" -> "Micah"
            "na" -> "Nahum"
            "hk" -> "Habakkuk"
            "zp" -> "Zephaniah"
            "hg" -> "Haggai"
            "zc" -> "Zechariah"
            "ml" -> "Malachi"
            "mt" -> "Matthew"
            "mk" -> "Mark"
            "lk" -> "Luke"
            "jo" -> "John"
            "act" -> "Acts"
            "rm" -> "Romans"
            "1co" -> "1 Corinthians"
            "2co" -> "2 Corinthians"
            "gl" -> "Galatians"
            "eph" -> "Ephesians"
            "ph" -> "Philippians"
            "cl" -> "Colossians"
            "1ts" -> "1 Thessalonians"
            "2ts" -> "2 Thessalonians"
            "1tm" -> "1 Timothy"
            "2tm" -> "2 Timothy"
            "tt" -> "Titus"
            "phm" -> "Philemon"
            "hb" -> "Hebrews"
            "jm" -> "James"
            "1pe" -> "1 Peter"
            "2pe" -> "2 Peter"
            "1jo" -> "1 John"
            "2jo" -> "2 John"
            "3jo" -> "3 John"
            "jd" -> "Jude"
            "re" -> "Revelation"
            else -> abbrev.replaceFirstChar { it.uppercase() }
        }
    }
}
