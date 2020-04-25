package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    companion object {
        var lessonDuration = 45
        var lessonSaturdayDuration = 45
    }

    private lateinit var lessonsDurationBar: SeekBar
    private lateinit var isSaturdayWorkingDay: CheckBox
    private lateinit var saturdayLessonDurationBar: SeekBar
    private lateinit var deleteLessons: Button
    private lateinit var deleteNotes: Button
    
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTheme(R.style.SettingsTheme)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        lessonsDurationBar = findViewById(R.id.lessonsDurationBar)
        isSaturdayWorkingDay = findViewById(R.id.isSaturdayWorkingDayCheckBox)
        saturdayLessonDurationBar = findViewById(R.id.saturdayLessonBar)
        deleteLessons = findViewById(R.id.deleteLessons)
        deleteNotes = findViewById(R.id.deleteNotes)

        val results = EnterActivity.readableDB.rawQuery("SELECT * FROM SETTINGS", null)
        if(results.count == 0) {
            val values = ContentValues()
            values.put("lessonsDuration", lessonDuration)
            values.put("isSaturdayWorkingDay", 1)
            values.put("saturdayLessonsDuration", lessonSaturdayDuration)
            EnterActivity.writableDB.insert("SETTINGS", null, values)
        }
        results.close()

        initViews()

        if(!checkSettings()) {
            lessonsDurationBar.progress = 45
            isSaturdayWorkingDay.isChecked = true
            saturdayLessonDurationBar.progress = 45
        }
        lessonsDurationTextView.text = "${getText(R.string.duration_of_lesson_text)} (${lessonsDurationBar.progress}  ${getText(R.string.min_text)})"
        lessonsSaturdayDurationTextView.text = "${getText(R.string.duration_of_lesson_on_saturday_text)} (${saturdayLessonDurationBar.progress}  ${getText(R.string.min_text)})"
    }

    private fun initViews() {
        lessonsDurationBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EnterActivity.writableDB.execSQL("UPDATE SETTINGS SET lessonsDuration='$progress' WHERE _id=1")
                lessonDuration = progress
                lessonsDurationTextView.text = "${getText(R.string.duration_of_lesson_text)} (${progress}  ${getText(R.string.min_text)})"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        isSaturdayWorkingDay.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    EnterActivity.writableDB.execSQL("UPDATE SETTINGS SET isSaturdayWorkingDay=1 WHERE _id=1")
                    saturdayLessonDurationBar.isEnabled = true
                }
                false -> {
                    EnterActivity.writableDB.execSQL("UPDATE SETTINGS SET isSaturdayWorkingDay=0 WHERE _id=1")
                    saturdayLessonDurationBar.isEnabled = false
                }
            }
        }
        saturdayLessonDurationBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EnterActivity.writableDB.execSQL("UPDATE SETTINGS SET saturdayLessonsDuration='$progress' WHERE _id=1")
                lessonSaturdayDuration = progress
                lessonsSaturdayDurationTextView.text = "${getText(R.string.duration_of_lesson_on_saturday_text)} (${progress} ${getText(R.string.min_text)})"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        deleteLessons.setOnClickListener {
            EnterActivity.writableDB.execSQL("DELETE FROM LESSONS_CARDS")
            Toast.makeText(applicationContext, R.string.all_lessons_deleted_text, Toast.LENGTH_LONG).show()
        }
        deleteNotes.setOnClickListener {
            EnterActivity.writableDB.execSQL("DELETE FROM NOTES")
            Toast.makeText(applicationContext, R.string.all_notes_deleted_text, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkSettings() : Boolean {
        val results = EnterActivity.readableDB.rawQuery("SELECT * FROM SETTINGS", null)
        return if(results.count >= 1) {
            results.moveToFirst()
            lessonsDurationBar.progress = results.getInt(results.getColumnIndex("lessonsDuration"))
            when(results.getInt(results.getColumnIndex("isSaturdayWorkingDay"))) {
                0 -> {
                    isSaturdayWorkingDay.isChecked = false
                    saturdayLessonDurationBar.isEnabled = false
                }
                1 -> {
                    isSaturdayWorkingDay.isChecked = true
                    saturdayLessonDurationBar.isEnabled = true
                }
            }
            saturdayLessonDurationBar.progress = results.getInt(results.getColumnIndex("saturdayLessonsDuration"))
            results.close()
            true
        } else {
            results.close()
            false
        }
    }
}