package com.example.bible_application.ui.verses

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.bible_application.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReadingSettingsBottomSheet : BottomSheetDialogFragment() {

    private var selectedTheme = "Light"
    private var selectedFontStyle = "Serif"
    private var selectedFontSize = 17f
    private var selectedLineSpacing = 1.3f
    private var selectedReadingAssist = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedTheme = it.getString("theme", "Light")
            selectedFontStyle = it.getString("font_style", "Serif")
            selectedFontSize = it.getFloat("font_size", 17f)
            selectedLineSpacing = it.getFloat("line_spacing", 1.3f)
            selectedReadingAssist = it.getBoolean("reading_assist", true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_reading_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close and dismiss buttons
        val btnClose = view.findViewById<ImageButton>(R.id.btn_close_settings)
        val btnBackReader = view.findViewById<TextView>(R.id.btn_back_to_reader)

        btnClose.setOnClickListener { dismiss() }
        btnBackReader.setOnClickListener { dismiss() }

        // Theme options
        val themeLight = view.findViewById<TextView>(R.id.choice_theme_light)
        val themeDark = view.findViewById<TextView>(R.id.choice_theme_dark)
        val themeSepia = view.findViewById<TextView>(R.id.choice_theme_sepia)

        // Font Style options
        val fontSerif = view.findViewById<TextView>(R.id.choice_font_serif)
        val fontSans = view.findViewById<TextView>(R.id.choice_font_sans)

        // Line Spacing options
        val spacingCompact = view.findViewById<TextView>(R.id.choice_spacing_compact)
        val spacingNormal = view.findViewById<TextView>(R.id.choice_spacing_normal)
        val spacingWide = view.findViewById<TextView>(R.id.choice_spacing_wide)

        // Reading Assist options
        val assistOff = view.findViewById<TextView>(R.id.choice_assist_off)
        val assistOn = view.findViewById<TextView>(R.id.choice_assist_on)

        // Font Size SeekBar
        val sizeSeekBar = view.findViewById<SeekBar>(R.id.dialog_font_size_seekbar)
        sizeSeekBar.progress = (selectedFontSize - 14f).toInt().coerceIn(0, 10)

        // 1. Setup Active selection visual states
        updateThemeUI(themeLight, themeDark, themeSepia)
        updateFontStyleUI(fontSerif, fontSans)
        updateSpacingUI(spacingCompact, spacingNormal, spacingWide)
        updateAssistUI(assistOff, assistOn)

        // 2. Add click observers and post changes dynamically using Fragment Result API
        themeLight.setOnClickListener {
            selectedTheme = "Light"
            updateThemeUI(themeLight, themeDark, themeSepia)
            postSettingsChange()
        }
        themeDark.setOnClickListener {
            selectedTheme = "Dark"
            updateThemeUI(themeLight, themeDark, themeSepia)
            postSettingsChange()
        }
        themeSepia.setOnClickListener {
            selectedTheme = "Sepia"
            updateThemeUI(themeLight, themeDark, themeSepia)
            postSettingsChange()
        }

        fontSerif.setOnClickListener {
            selectedFontStyle = "Serif"
            updateFontStyleUI(fontSerif, fontSans)
            postSettingsChange()
        }
        fontSans.setOnClickListener {
            selectedFontStyle = "Sans"
            updateFontStyleUI(fontSerif, fontSans)
            postSettingsChange()
        }

        spacingCompact.setOnClickListener {
            selectedLineSpacing = 1.1f
            updateSpacingUI(spacingCompact, spacingNormal, spacingWide)
            postSettingsChange()
        }
        spacingNormal.setOnClickListener {
            selectedLineSpacing = 1.3f
            updateSpacingUI(spacingCompact, spacingNormal, spacingWide)
            postSettingsChange()
        }
        spacingWide.setOnClickListener {
            selectedLineSpacing = 1.6f
            updateSpacingUI(spacingCompact, spacingNormal, spacingWide)
            postSettingsChange()
        }

        assistOff.setOnClickListener {
            selectedReadingAssist = false
            updateAssistUI(assistOff, assistOn)
            postSettingsChange()
        }
        assistOn.setOnClickListener {
            selectedReadingAssist = true
            updateAssistUI(assistOff, assistOn)
            postSettingsChange()
        }

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedFontSize = 14f + progress
                postSettingsChange()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun postSettingsChange() {
        val bundle = Bundle().apply {
            putString("theme", selectedTheme)
            putString("font_style", selectedFontStyle)
            putFloat("font_size", selectedFontSize)
            putFloat("line_spacing", selectedLineSpacing)
            putBoolean("reading_assist", selectedReadingAssist)
        }
        parentFragmentManager.setFragmentResult("settings_key", bundle)
    }

    private fun updateThemeUI(light: TextView, dark: TextView, sepia: TextView) {
        setChoiceState(light, selectedTheme == "Light")
        setChoiceState(dark, selectedTheme == "Dark")
        setChoiceState(sepia, selectedTheme == "Sepia")
    }

    private fun updateFontStyleUI(serif: TextView, sans: TextView) {
        setChoiceState(serif, selectedFontStyle == "Serif")
        setChoiceState(sans, selectedFontStyle == "Sans")
    }

    private fun updateSpacingUI(compact: TextView, normal: TextView, wide: TextView) {
        setChoiceState(compact, selectedLineSpacing == 1.1f)
        setChoiceState(normal, selectedLineSpacing == 1.3f)
        setChoiceState(wide, selectedLineSpacing == 1.6f)
    }

    private fun updateAssistUI(off: TextView, on: TextView) {
        setChoiceState(off, !selectedReadingAssist)
        setChoiceState(on, selectedReadingAssist)
    }

    private fun setChoiceState(view: TextView, isActive: Boolean) {
        if (isActive) {
            view.setBackgroundResource(R.drawable.bg_chapter_active)
            view.setTextColor(Color.parseColor("#1C3AA9")) // Beautiful selected active deep blue text color!
            view.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            view.setBackgroundResource(R.drawable.bg_chapter_normal)
            view.setTextColor(Color.parseColor("#616161"))
            view.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    companion object {
        fun newInstance(
            theme: String,
            fontStyle: String,
            fontSize: Float,
            lineSpacing: Float,
            readingAssist: Boolean
        ): ReadingSettingsBottomSheet {
            return ReadingSettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("theme", theme)
                    putString("font_style", fontStyle)
                    putFloat("font_size", fontSize)
                    putFloat("line_spacing", lineSpacing)
                    putBoolean("reading_assist", readingAssist)
                }
            }
        }
    }
}
