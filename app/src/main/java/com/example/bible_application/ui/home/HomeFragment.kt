package com.example.bible_application.ui.home

import android.content.Intent
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
import com.example.bible_application.R
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val textDate = view.findViewById<TextView>(R.id.daily_verse_date)
        val textContent = view.findViewById<TextView>(R.id.daily_verse_content)
        val textReference = view.findViewById<TextView>(R.id.daily_verse_reference_chip)
        val btnBookmark = view.findViewById<ImageButton>(R.id.btn_bookmark)
        val btnShare = view.findViewById<ImageButton>(R.id.btn_share)

        // Set dynamic current system date on homepage
        val sdf = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault())
        textDate.text = sdf.format(java.util.Date())

        // Observe Daily Verse from database
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyVerse.collect { verse ->
                if (verse != null) {
                    textContent.text = verse.text
                    textReference.text = "${verse.bookName} ${verse.chapter}:${verse.verseNumber}"

                    // Set filled/outline bookmark icon state
                    if (verse.isBookmarked) {
                        btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                    } else {
                        btnBookmark.setImageResource(R.drawable.ic_bookmark_border)
                    }

                    // Tapping capsule chip views the verse in context
                    textReference.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("bookId", verse.bookId)
                            putString("bookName", verse.bookName)
                            putInt("chapter", verse.chapter)
                        }
                        findNavController().navigate(R.id.action_home_to_verses, bundle)
                    }

                    // Toggle bookmark click
                    btnBookmark.setOnClickListener {
                        viewModel.toggleBookmark()
                    }

                    // Share click
                    btnShare.setOnClickListener {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "\"${verse.text}\"\n\n— ${verse.bookName} ${verse.chapter}:${verse.verseNumber}"
                            )
                        }
                        startActivity(Intent.createChooser(shareIntent, "Share Scripture"))
                    }
                }
            }
        }
    }
}
