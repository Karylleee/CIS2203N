package com.example.bible_application.ui.verses

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bible_application.R
import kotlinx.coroutines.launch

class VersesFragment : Fragment() {

    private lateinit var viewModel: VersesViewModel
    private lateinit var adapter: VersesAdapter
    private var bookId: Int = -1
    private var bookName: String = ""
    private var chapter: Int = -1
    private var totalChapters: Int = 1

    // Reading Display setting variables
    private var activeTheme = "Light"
    private var activeFontStyle = "Serif"
    private var activeFontSize = 17f
    private var activeLineSpacing = 1.3f
    private var activeReadingAssist = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getInt("bookId", -1)
            bookName = it.getString("bookName", "")
            chapter = it.getInt("chapter", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_verses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[VersesViewModel::class.java]

        val backButton = view.findViewById<ImageButton>(R.id.btn_back_verses)
        val closeButton = view.findViewById<ImageButton>(R.id.btn_close_verses)
        val bookTitle = view.findViewById<TextView>(R.id.verses_book_title)
        val bookSubtitle = view.findViewById<TextView>(R.id.verses_book_subtitle)
        val textCounter = view.findViewById<TextView>(R.id.text_chapter_counter)

        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev_chapter)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next_chapter)
        val btnSettings = view.findViewById<ImageButton>(R.id.btn_reading_settings)
        val fontSizeSeekBar = view.findViewById<SeekBar>(R.id.font_size_seekbar)

        // Bind initial layouts
        bookTitle.text = "$bookName $chapter"
        bookSubtitle.text = viewModel.getBookTestament(bookId)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.verses_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create adapter with dynamic display configurations
        adapter = VersesAdapter(
            emptyList(),
            textSizeSp = activeFontSize,
            fontStyle = activeFontStyle,
            lineSpacing = activeLineSpacing,
            theme = activeTheme
        ) { verse ->
            val defaultColor = "#FFF9C4"
            viewModel.toggleVerseHighlight(verse, defaultColor)
        }
        recyclerView.adapter = adapter

        // Bind layout Seekbar progress changes
        fontSizeSeekBar.progress = (activeFontSize - 14f).toInt().coerceIn(0, 10)
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                activeFontSize = 14f + progress
                adapter.updateDisplaySettings(activeFontSize, activeFontStyle, activeLineSpacing, activeTheme)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Open Bottom Sheet settings when Gear is clicked
        btnSettings.setOnClickListener {
            val settingsSheet = ReadingSettingsBottomSheet.newInstance(
                theme = activeTheme,
                fontStyle = activeFontStyle,
                fontSize = activeFontSize,
                lineSpacing = activeLineSpacing,
                readingAssist = activeReadingAssist
            )
            settingsSheet.show(childFragmentManager, "ReadingSettings")
        }

        // Listen for setting changes from Bottom Sheet
        childFragmentManager.setFragmentResultListener("settings_key", viewLifecycleOwner) { _, bundle ->
            activeTheme = bundle.getString("theme", "Light")
            activeFontStyle = bundle.getString("font_style", "Serif")
            activeFontSize = bundle.getFloat("font_size", 17f)
            activeLineSpacing = bundle.getFloat("line_spacing", 1.3f)
            activeReadingAssist = bundle.getBoolean("reading_assist", true)

            // Update main seekBar progress
            fontSizeSeekBar.progress = (activeFontSize - 14f).toInt().coerceIn(0, 10)

            // Notify adapter to update displays
            adapter.updateDisplaySettings(activeFontSize, activeFontStyle, activeLineSpacing, activeTheme)

            // Apply theme changes to parent container dynamically
            applyThemeChanges(view, bookTitle, bookSubtitle, textCounter, btnPrev, btnNext, backButton, closeButton, btnSettings)
        }

        // Fetch chapter counts
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getChaptersCountFlow(bookId).collect { chapters ->
                totalChapters = chapters.size
                updateChapterControls(bookTitle, textCounter, btnPrev, btnNext)
            }
        }

        loadVerses()
    }

    private fun loadVerses() {
        viewModel.markChapterAsRead(bookId, chapter)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getVersesFlow(bookId, chapter).collect { list ->
                adapter.updateVerses(list)
            }
        }
    }

    private fun applyThemeChanges(
        rootView: View,
        title: TextView,
        subtitle: TextView,
        counter: TextView,
        btnPrev: ImageButton,
        btnNext: ImageButton,
        btnBack: ImageButton,
        btnClose: ImageButton,
        btnSettings: ImageButton
    ) {
        val bgColor: Int
        val textColor: Int
        val subTextColor: Int

        when (activeTheme) {
            "Dark" -> {
                bgColor = Color.parseColor("#121212")
                textColor = Color.parseColor("#FFFFFF")
                subTextColor = Color.parseColor("#B0BEC5")

                val darkIconColor = Color.parseColor("#FFFFFF")
                btnPrev.setColorFilter(darkIconColor)
                btnNext.setColorFilter(darkIconColor)
                btnBack.setColorFilter(darkIconColor)
                btnClose.setColorFilter(darkIconColor)
                btnSettings.setColorFilter(darkIconColor)
            }
            "Sepia" -> {
                bgColor = Color.parseColor("#F4ECD8")
                textColor = Color.parseColor("#5B4636")
                subTextColor = Color.parseColor("#8D6E63")

                val sepiaIconColor = Color.parseColor("#5B4636")
                btnPrev.setColorFilter(sepiaIconColor)
                btnNext.setColorFilter(sepiaIconColor)
                btnBack.setColorFilter(sepiaIconColor)
                btnClose.setColorFilter(sepiaIconColor)
                btnSettings.setColorFilter(sepiaIconColor)
            }
            else -> { // Light Theme
                bgColor = Color.parseColor("#FFFFFF")
                textColor = Color.parseColor("#1E1E1E")
                subTextColor = Color.parseColor("#9E9E9E")

                val lightIconColor = Color.parseColor("#1E1E1E")
                btnPrev.setColorFilter(lightIconColor)
                btnNext.setColorFilter(lightIconColor)
                btnBack.setColorFilter(lightIconColor)
                btnClose.setColorFilter(lightIconColor)
                btnSettings.setColorFilter(lightIconColor)
            }
        }

        // Update root and headers color templates
        rootView.setBackgroundColor(bgColor)
        title.setTextColor(textColor)
        subtitle.setTextColor(subTextColor)
        counter.setTextColor(textColor)
    }

    private fun updateChapterControls(
        bookTitle: TextView,
        textCounter: TextView,
        btnPrev: ImageButton,
        btnNext: ImageButton
    ) {
        bookTitle.text = "$bookName $chapter"
        textCounter.text = "$chapter of $totalChapters"

        btnPrev.isEnabled = chapter > 1
        btnPrev.alpha = if (chapter > 1) 1.0f else 0.4f
        btnPrev.setOnClickListener {
            if (chapter > 1) {
                chapter--
                updateChapterControls(bookTitle, textCounter, btnPrev, btnNext)
                loadVerses()
            }
        }

        btnNext.isEnabled = chapter < totalChapters
        btnNext.alpha = if (chapter < totalChapters) 1.0f else 0.4f
        btnNext.setOnClickListener {
            if (chapter < totalChapters) {
                chapter++
                updateChapterControls(bookTitle, textCounter, btnPrev, btnNext)
                loadVerses()
            }
        }
    }
}
