package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.activity_enter.*
import okhttp3.OkHttpClient

/**
 * Активность, которая вызывается при входе
 */
class EnterActivity : AppCompatActivity() {

    companion object{
        /**Helper БД*/
        @JvmStatic
        lateinit var helper: DBHelper
        /**Переменная, хранящая readable БД*/
        @JvmStatic
        lateinit var readableDB: SQLiteDatabase
        /**Переменная, хранящая writable БД*/
        @JvmStatic
        lateinit var writableDB: SQLiteDatabase

        @JvmStatic
        fun createHelper(context: Context) {
            helper = DBHelper(context, "DiaryDB", null, 5)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //applicationContext.deleteDatabase("DiaryDB")

        //Убрать блок комментариев, когда нужно стереть данные из SQLite
        /*writableDB.execSQL("delete from LESSONS_CARDS")
        writableDB.execSQL("delete from TIMETABLE")
        writableDB.execSQL("delete from NOTES")*/

        Stetho.initializeWithDefaults(this)
        OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        createHelper(applicationContext)
        readableDB = helper.readableDatabase
        writableDB = helper.writableDatabase

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
            startActivity(Intent(this, NotesActivity::class.java))
        }

        val display = windowManager.defaultDisplay
        diary_layout.layoutParams = ConstraintLayout.LayoutParams(display.width, display.height/2 + 65)
        notes_layout.layoutParams = ConstraintLayout.LayoutParams(display.width, display.height/2 + 65)
        notes_layout.y = diary_layout.y + diary_layout.height + display.height / 2
        notes_textview.setTypeface(null, Typeface.BOLD)

    }
}