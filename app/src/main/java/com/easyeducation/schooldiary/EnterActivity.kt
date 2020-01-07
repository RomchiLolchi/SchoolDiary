package com.easyeducation.schooldiary

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_enter.*

/**
 * Активность, которая вызывается при входе
 */
class EnterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val diaryText = findViewById<TextView>(R.id.diary_textview)
            val diaryDesText = findViewById<TextView>(R.id.diary_description_textview)
            val notesText = findViewById<TextView>(R.id.notes_textview)
            val notesDesText = findViewById<TextView>(R.id.notes_textview)

            val fontBold = resources.getFont(R.font.sf_ui_display_bold)
            val fontLight = resources.getFont(R.font.sf_ui_display_light)

            diaryText.typeface = fontBold
            notesText.typeface = fontBold
            diaryDesText.typeface = fontLight
            notesDesText.typeface = fontLight
        }

        val buttonToDiary = findViewById<ImageView>(R.id.image_open_diary)
        buttonToDiary.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }
        val buttonToNotes = findViewById<ImageView>(R.id.image_open_notes)
        buttonToNotes.setOnClickListener {
            Toast.makeText(applicationContext, "Скоро будет...", Toast.LENGTH_LONG).show()
        }

        val display = windowManager.defaultDisplay
        diary_layout.layoutParams = ConstraintLayout.LayoutParams(display.width, display.height/2 + 65)
        notes_layout.layoutParams = ConstraintLayout.LayoutParams(display.width, display.height/2 + 65)
        notes_layout.y = diary_layout.y + diary_layout.height + display.height / 2
        notes_textview.setTypeface(null, Typeface.BOLD)

    }
}
