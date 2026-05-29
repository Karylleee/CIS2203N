package com.example.bible_application.ui.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import kotlinx.coroutines.launch

class ChaptersFragment : Fragment() {

    private lateinit var viewModel: ChaptersViewModel
    private lateinit var adapter: ChaptersAdapter
    private var bookId: Int = -1
    private var bookName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getInt("bookId", -1)
            bookName = it.getString("bookName", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chapters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChaptersViewModel::class.java]

        val backButton = view.findViewById<ImageButton>(R.id.btn_back_chapters)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val btnBookmarkBook = view.findViewById<ImageButton>(R.id.btn_bookmark_book)
        viewModel.loadFirstVerse(bookId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.firstVerse.collect { verse ->
                if (verse != null) {
                    if (verse.isBookmarked) {
                        btnBookmarkBook.setImageResource(R.drawable.ic_bookmark_filled)
                    } else {
                        btnBookmarkBook.setImageResource(R.drawable.ic_bookmark_border)
                    }
                }
            }
        }
        btnBookmarkBook.setOnClickListener {
            viewModel.toggleBookBookmark()
        }

        val bookTitle = view.findViewById<TextView>(R.id.chapters_book_title)
        val bookSubtitle = view.findViewById<TextView>(R.id.chapters_book_subtitle)
        val bookDesc = view.findViewById<TextView>(R.id.about_book_description)
        val totalCountText = view.findViewById<TextView>(R.id.chapters_total_count)

        bookTitle.text = bookName
        bookDesc.text = viewModel.getBookSummary(bookName)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chapters_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 5)

        val initialReadChapters = viewModel.getReadChapters(bookId)
        val initialLastRead = viewModel.getLastReadChapter(bookId)

        adapter = ChaptersAdapter(emptyList(), initialReadChapters, initialLastRead) { chapter ->
            viewModel.markChapterAsRead(bookId, chapter)
            val bundle = Bundle().apply {
                putInt("bookId", bookId)
                putString("bookName", bookName)
                putInt("chapter", chapter)
            }
            findNavController().navigate(R.id.action_chapters_to_verses, bundle)
        }
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getChaptersFlow(bookId).collect { list ->
                val readChapters = viewModel.getReadChapters(bookId)
                val lastRead = viewModel.getLastReadChapter(bookId)
                adapter.updateChapters(list, readChapters, lastRead)

                // Populate dynamic book counts and testaments
                val testament = viewModel.getBookTestament(bookId)
                bookSubtitle.text = "$testament · ${list.size} Ch"
                totalCountText.text = "${list.size} total"
            }
        }

        val lastReadPill = view.findViewById<View>(R.id.btn_last_read_pill)
        lastReadPill.setOnClickListener {
            val lastRead = viewModel.getLastReadChapter(bookId)
            val targetChapter = if (lastRead != -1) lastRead else 1
            viewModel.markChapterAsRead(bookId, targetChapter)
            val bundle = Bundle().apply {
                putInt("bookId", bookId)
                putString("bookName", bookName)
                putInt("chapter", targetChapter)
            }
            findNavController().navigate(R.id.action_chapters_to_verses, bundle)
        }
    }
}
