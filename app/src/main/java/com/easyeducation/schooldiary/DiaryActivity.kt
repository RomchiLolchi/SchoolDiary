package com.easyeducation.schooldiary


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.easyeducation.schooldiary.EnterActivity.Companion.createHelper
import com.easyeducation.schooldiary.EnterActivity.Companion.helper
import com.easyeducation.schooldiary.EnterActivity.Companion.readableDB
import com.easyeducation.schooldiary.EnterActivity.Companion.writableDB
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.activity_diary.view.*
import okhttp3.OkHttpClient
import java.util.*
import kotlin.collections.ArrayList


/**
 * Главный фрагмент дневника
 */
class DiaryActivity : AppCompatActivity() {

    /**Переменная layout'а нового пользователя*/
    lateinit var newUserLayout: ConstraintLayout
    /**Переменная layout'а старого пользователя*/
    lateinit var oldUserLayout: ConstraintLayout
    /**Переменная, хранящая значение количества ratingBar'ов*/
    var ratingBarsAmount = 1
    /**2-ой ratingBar*/
    private lateinit var ratingBar2: RatingBar
    //Переменные для метода prepareButtons
    private lateinit var selColor: ImageView
    private lateinit var calendar: Calendar

    companion object {

        /**Переменная layout'а создания урока*/
        @JvmStatic
        lateinit var createLessonLayout: ConstraintLayout

        lateinit var addLesson: Button

        /**Переменные времени*/
        @JvmStatic
        var year2 = 2005
        @JvmStatic
        var dayMonth = 0
        @JvmStatic
        var month1 = 7
        @JvmStatic
        var dayOfWeek1 = 0

        /**Переменная, хранящая значение даты для карточки*/
        @JvmStatic
        var date = "date"
        /**Переменная, хранящая значение времени для карточки*/
        @JvmStatic
        var time = "time"

        /**Переменная, хранящая цвет карточек*/
        @JvmStatic
        var color = "white"

        /**Переменные даты и времени*/
        @JvmStatic
        var day = 0
        @JvmStatic
        var month = 0
        @JvmStatic
        var year1 = 0
        @JvmStatic
        var minutes = ""
        @JvmStatic
        var hours = 0
        @JvmStatic
        var lessonDayOfWeek = 2

        /**Переменные для показа DatePickerDialog и TimePickerDialog*/
        var hours1 = 12
        var minutes1 = 0
        var date1 = 15


    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
        setTheme(R.style.DiaryTheme)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /**Переменная view для вызова findViewById()*/
        newUserLayout = findViewById(R.id.new_user_layout)!!
        oldUserLayout = findViewById(R.id.old_user_layout)!!
        createLessonLayout = findViewById(R.id.create_lesson_layout)!!

        calendar = Calendar.getInstance()
        dayMonth = calendar.get(Calendar.DAY_OF_MONTH)
        date1 = calendar.get(Calendar.DATE)
        calendar.get(Calendar.DAY_OF_MONTH)
        month1 = calendar.get(Calendar.MONTH)
        year2 = calendar.get(Calendar.YEAR)
        hours1 = calendar.get(Calendar.HOUR_OF_DAY)
        minutes1 = calendar.get(Calendar.MINUTE)

        ratingBar2 = findViewById(R.id.ratingBarTwo)
        ratingBar2.isClickable = false
        ratingBar2.visibility = View.INVISIBLE

        prepareButtons(false)

        val addLessonOldUser = findViewById<Button>(R.id.add_lesson_olduser_button)
        addLessonOldUser.setOnClickListener{
            setVisParams(true, createLessonLayout)
            main_layout.isClickable = false
            prepareButtons(false)
            prepareButtons(needToClear = true)
        }

        if(isOldUser()) {
            //Старый пользователь
            setVisParams(false, newUserLayout)
            setVisParams(false, createLessonLayout)
            setVisParams(true, oldUserLayout)
            onOldUser()
        } else {
            //Новый пользователь
            setVisParams(true, newUserLayout)
            setVisParams(false, createLessonLayout)
            setVisParams(false, oldUserLayout)
        }
    }

    override fun onBackPressed() {
        if(isOldUser() || newUserLayout.visibility == View.VISIBLE) {
            if (createLessonLayout.visibility == View.VISIBLE) {
                setVisParams(false, createLessonLayout)
            } else {
                startActivity(Intent(this, EnterActivity::class.java))
            }
        }
        else{
            startActivity(Intent(this, EnterActivity::class.java))
        }
    }

    /**
     * Метод для настройки параметров setVisibility и setClickable
     * @param param Параметр для настройки видимости объекта
     * @param obj Параметр для приёма объекта, который будет настраиваться
     */
    private fun setVisParams(param: Boolean, obj: ConstraintLayout) {
        when(param){
            true -> {
                when(obj){
                    createLessonLayout -> {
                        createLessonLayout.visibility = View.VISIBLE
                        createLessonLayout.isClickable = true

                        Log.d("LayoutManager", "Layout: createLessonLayout, param: true")
                    }
                    newUserLayout -> {
                        newUserLayout.visibility = View.VISIBLE
                        newUserLayout.isClickable = true
                        Log.d("LayoutManager", "Layout: newUserLayout, param: true")
                    }
                    oldUserLayout -> {
                        oldUserLayout.visibility = View.VISIBLE
                        oldUserLayout.isClickable = true
                        Log.d("LayoutManager", "Layout: oldUserLayout, param: true")
                    }
                }
            }
            false -> {
                when(obj){
                    createLessonLayout -> {
                        createLessonLayout.visibility = View.INVISIBLE
                        createLessonLayout.isClickable = false
                        Log.d("LayoutManager", "Layout: oldUserLayout, param: false")

                    }
                    newUserLayout -> {
                        newUserLayout.visibility = View.INVISIBLE
                        newUserLayout.isClickable = false
                        Log.d("LayoutManager", "Layout: oldUserLayout, param: false")
                    }
                    oldUserLayout -> {
                        oldUserLayout.visibility = View.INVISIBLE
                        oldUserLayout.isClickable = false
                        Log.d("LayoutManager", "Layout: oldUserLayout, param: false")
                    }
                }
            }
        }
    }

    /**
     * Метод для назначения onClickLister'ов кнопкам layout'а создания урока
     * @param needToClear Надо ли "почистить" поля?
     */
    @SuppressLint("NewApi")
    private fun prepareButtons(needToClear: Boolean){
        if(needToClear){
            createLessonLayout.background = ContextCompat.getDrawable(applicationContext, R.drawable.white_border)
            selColor.setImageDrawable(resources.getDrawable(R.drawable.white_rect, null))
            color_scroll.visibility = View.INVISIBLE
            color_scroll.isClickable = false
            ratingBar.rating = 0F
            if (ratingBarsAmount > 1) {
                ratingBar2.isClickable = false
                ratingBar2.visibility = View.INVISIBLE
                ratingBar2.rating = 0F
                ratingBarsAmount--
            }
            color_scroll.verticalScrollbarPosition = ScrollView.SCROLLBAR_POSITION_LEFT
            lesson_date_edittext.text = resources.getText(R.string.choose_date_text)
            lesson_time_edittext.text = resources.getText(R.string.choose_time_text)
            order_lesson_editText.text = SpannableStringBuilder("")
            homework_edittext.text = SpannableStringBuilder("")
            lesson_name_edittext.text = SpannableStringBuilder("")
            lesson_teacher_edittext.text = SpannableStringBuilder("")
            color = "white"
        }
        else {
            val newLesson = findViewById<Button>(R.id.new_lesson_button)
            newLesson.setOnClickListener {
                setVisParams(true, createLessonLayout)
                main_layout.isClickable = false
                prepareButtons(needToClear = true)
            }

            lesson_name_edittext.setOnEditorActionListener { v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                    return@setOnEditorActionListener true
                }
                else {
                    return@setOnEditorActionListener false
                }
            }

            lesson_teacher_edittext.setOnEditorActionListener { v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                    return@setOnEditorActionListener true
                }
                else {
                    return@setOnEditorActionListener false
                }
            }

            homework_edittext.setOnEditorActionListener { v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                    return@setOnEditorActionListener true
                }
                else {
                    return@setOnEditorActionListener false
                }
            }

            order_lesson_editText.setOnEditorActionListener { v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                    return@setOnEditorActionListener true
                }
                else {
                    return@setOnEditorActionListener false
                }
            }

            //Переменная для правильной работы скролла и кнопки
            var isOpen = false

            color_scroll.visibility = View.INVISIBLE
            color_scroll.isClickable = false

            selColor = findViewById(R.id.select_color_image)
            selColor.setOnClickListener {
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                if (isOpen) {
                    color_scroll.visibility = View.INVISIBLE
                    color_scroll.isClickable = false
                    isOpen = false
                } else {
                    color_scroll.visibility = View.VISIBLE
                    color_scroll.isClickable = true
                    isOpen = true
                }
            }

            val whiteColor = findViewById<ImageView>(R.id.white_sel_image)
            whiteColor.setOnClickListener {
                createLessonLayout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.white_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.white_rect, null))
                color = "white"
                isOpen = false
            }

            val redColor = findViewById<ImageView>(R.id.red_sel_image)
            redColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.red_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.red_rect, null))
                color = "red"
                isOpen = false
            }

            val orangeColor = findViewById<ImageView>(R.id.orange_sel_image)
            orangeColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.orange_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.orange_rect, null))
                color = "orange"
                isOpen = false
            }

            val yellowColor = findViewById<ImageView>(R.id.yellow_sel_image)
            yellowColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.yellow_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.yellow_rect, null))
                color = "yellow"
                isOpen = false
            }

            val greenColor = findViewById<ImageView>(R.id.green_sel_image)
            greenColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.green_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.green_rect, null))
                color = "green"
                isOpen = false
            }

            val blueColor = findViewById<ImageView>(R.id.blue_sel_image)
            blueColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.blue_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.blue_rect, null))
                color = "blue"
                isOpen = false
            }

            val pinkColor = findViewById<ImageView>(R.id.pink_sel_image)
            pinkColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.pink_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.pink_rect, null))
                color = "pink"
                isOpen = false
            }

            val violetColor = findViewById<ImageView>(R.id.violet_sel_image)
            violetColor.setOnClickListener {
                create_lesson_layout.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.violet_border)
                color_scroll.visibility = View.INVISIBLE
                color_scroll.isClickable = false
                selColor.setImageDrawable(resources.getDrawable(R.drawable.violet_rect, null))
                color = "violet"
                isOpen = false
            }

            val addAttach = findViewById<ImageView>(R.id.add_attachment_imageview)
            addAttach.setOnClickListener {
                //TODO Нужно внедрить "технологию" attachment'ов
                Toast.makeText(applicationContext, applicationContext.resources.getString(R.string.will_be_added_soon), Toast.LENGTH_SHORT).show()
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            val deleteAttach = findViewById<ImageView>(R.id.delete_attachment_imageview)
            deleteAttach.setOnClickListener {
                Toast.makeText(applicationContext, applicationContext.resources.getString(R.string.will_be_added_soon), Toast.LENGTH_SHORT).show()
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            val addRate = findViewById<ImageView>(R.id.add_rate_imageview)
            addRate.setOnClickListener {
                if (ratingBarsAmount < 2) {
                    ratingBar2.isClickable = true
                    ratingBar2.visibility = View.VISIBLE
                    ratingBarsAmount++
                } else {
                    Toast.makeText(
                        applicationContext,
                        resources.getText(R.string.max_ratings_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            val deleteRate = findViewById<ImageView>(R.id.delete_rate_imageview)
            deleteRate.setOnClickListener {
                if (ratingBarsAmount > 1) {
                    ratingBar2.isClickable = false
                    ratingBar2.visibility = View.INVISIBLE
                    ratingBar2.rating = 0F
                    ratingBarsAmount--
                }
                else {
                    ratingBar.rating = 0F
                }
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            val dateEditText = findViewById<TextView>(R.id.lesson_date_edittext)
            dateEditText.setOnClickListener {
                /*val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)*/
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                val myCallBack = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    day = dayOfMonth
                    month = monthOfYear + 1
                    year1 = year
                    date = "$dayOfMonth.$monthOfYear.$year"

                    lesson_date_edittext.text = SpannableStringBuilder("${resources.getText(R.string.date_text)} $day.$month.$year1")
                }
                createLessonLayout.post {
                    DatePickerDialog(this@DiaryActivity, myCallBack, year2, month1, date1).apply {
                        show()
                    }
                }
            }

            val timeEditText = findViewById<TextView>(R.id.lesson_time_edittext)
            timeEditText.setOnClickListener {
                /*val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)*/
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                val myCallBack = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    hours = hourOfDay
                    minutes = minute.toString()

                    Log.d("Minutes of lesson", "$minute")

                    if(minute in 0..9){
                        when(minute){
                            0 -> {
                                minutes = "00"
                            }
                            1 -> {
                                minutes = "01"
                            }
                            2 -> {
                                minutes = "02"
                            }
                            3 -> {
                                minutes = "03"
                            }
                            4 -> {
                                minutes = "04"
                            }
                            5 -> {
                                minutes = "05"
                            }
                            6 -> {
                                minutes = "06"
                            }
                            7 -> {
                                minutes = "07"
                            }
                            8 -> {
                                minutes = "08"
                            }
                            9 -> {
                                minutes = "09"
                            }
                        }
                    }

                    time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(hourOfDay.toString().length == 1){
                            "0${view.hour}:$minutes"
                        }
                        else {
                            "${view.hour}:$minutes"
                        }
                    } else {
                        if(hourOfDay.toString().length == 1) {
                            "0$hourOfDay:$minutes"
                        }
                        else{
                            "$hourOfDay:$minutes"
                        }
                    }
                    lesson_time_edittext.text = SpannableStringBuilder("${resources.getText(R.string.time_text)} $time")
                }
                createLessonLayout.post {
                    TimePickerDialog(this@DiaryActivity, myCallBack, hours1, minutes1, false).show()
                }
            }

            addLesson = findViewById(R.id.add_lesson_button)
            addLesson.text = applicationContext.resources.getString(R.string.add_lesson_text)
            addLesson.setOnClickListener {
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                if(time == "time"){
                    time = "12:0"
                }
                if(date == "date"){
                    date = "1.0.2019"
                }
                if(order_lesson_editText.text.toString().isNullOrBlank() || order_lesson_editText.text.toString().isNullOrEmpty()){
                    order_lesson_editText.setText("1")
                }

                val values = ContentValues()
                values.put("name", lesson_name_edittext.text.toString())
                values.put("color", color)
                values.put("date", date)
                val cal = Calendar.getInstance()
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.minimalDaysInFirstWeek = 4
                cal.set(Calendar.YEAR, year1)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                if(minutes.isNullOrBlank() || minutes.isNullOrEmpty()){
                    minutes = "0"
                }
                cal.set(Calendar.MINUTE, minutes.toInt())
                if(hours == null){
                    hours = 0
                }
                cal.set(Calendar.HOUR_OF_DAY, hours)
                lessonDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                //Toast.makeText(applicationContext, lessonDayOfWeek, Toast.LENGTH_SHORT).show()
                values.put("dayOfWeek", lessonDayOfWeek)
                values.put("time", time)
                values.put("teacher", lesson_teacher_edittext.text.toString())
                values.put("ratingOne", ratingBar.rating)
                values.put("ratingTwo", ratingBar2.rating)
                values.put("homework", homework_edittext.text.toString())
                values.put("lessonsOrder", order_lesson_editText.text.toString())
                //writableDB.insert("LESSONS_CARDS", null, values)
                /*writableDB.execSQL("INSERT INTO LESSONS_CARDS " +
                                        "VALUES( \"${values.get("order")}\", \"${values.get("name")}\", \"${values.get("color")}\", \"${values.get("date")}\", \"${values.get("dayOfWeek")}\", \"${values.get("time")}\", " +
                                        "\"${values.get("teacher")}\", \"${values.get("ratingOne")}\", \"${values.get("ratingTwo")}\", \"${values.get("homework")}\");")*/
                //writableDB.close()

                writableDB.insertOrThrow("LESSONS_CARDS", null, values)

                Log.d("DB debug", "Data of lesson in content values:\n" +
                        "name: ${values.get("name")}\n" +
                        "color: ${values.get("color")}\n" +
                        "date: ${values.get("date")}\n" +
                        "dayOfWeek: ${values.get("dayOfWeek")}\n" +
                        "time: ${values.get("time")}\n" +
                        "teacher: ${values.get("teacher")}\n" +
                        "order: ${values.get("lessonsOrder")}\n" +
                        "ratingOne: ${values.get("ratingOne")}\n" +
                        "ratingTwo: ${values.get("ratingTwo")}\n" +
                        "homework: ${values.get("homework")}")

                if(isOldUser()) {
                    //Старый пользователь
                    setVisParams(false, newUserLayout)
                    setVisParams(false, createLessonLayout)
                    setVisParams(true, oldUserLayout)
                    onOldUser()
                } else {
                    //Новый пользователь
                    setVisParams(true, newUserLayout)
                    setVisParams(false, createLessonLayout)
                    setVisParams(false, oldUserLayout)
                }

                Toast.makeText(applicationContext, resources.getText(R.string.lesson_add_text), Toast.LENGTH_LONG).show()

                val cursor = readableDB.rawQuery("SELECT name FROM TEACHERS", null)
                if (cursor.moveToFirst() && cursor != null) {
                    val arrayTeachers = ArrayList<String>()
                    if (!cursor.isLast) {
                        for (i in 0..cursor.columnCount) {
                            if (!cursor.isLast) {
                                arrayTeachers.add(cursor.getString(cursor.getColumnIndex("name")))
                            } else {
                                arrayTeachers.add(cursor.getString(cursor.getColumnIndex("name")))
                                break
                            }
                            cursor.moveToNext()
                        }
                        if (arrayTeachers.isNotEmpty()) {
                            for (i in 0..arrayTeachers.size) {
                                if (arrayTeachers[i] != lesson_teacher_edittext.text.toString()) {
                                    val valuesTeachers1 = ContentValues()
                                    valuesTeachers1.put(
                                        "name",
                                        lesson_teacher_edittext.text.toString()
                                    )
                                    writableDB.insert("TEACHERS", null, valuesTeachers1)
                                    break
                                }
                            }
                        }
                    } else {
                        if (cursor.getString(cursor.getColumnIndex("name")) != lesson_teacher_edittext.text.toString()) {
                            val valuesTeachers = ContentValues()
                            valuesTeachers.put("name", lesson_teacher_edittext.text.toString())
                            writableDB.insert("TEACHERS", null, valuesTeachers)
                        }
                    }
                }
                cursor.close()
                setVisParams(false, createLessonLayout)
                prepareButtons(needToClear = true)
            }
        }

    }

    /**
     * Метод, для проверки на старого и нового пользователя
     * @return Возвращает значение true - старый, false - новый
     */
    private fun isOldUser() : Boolean {
        var curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS", null)
        curs.moveToFirst()
        if(curs.count > 0 /*&& curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()*/){
            Log.d("DB debug", "First line in table: ${curs.getString(curs.getColumnIndex("name"))}")
            curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS WHERE dayOfWeek=5", null)
            curs.moveToFirst()
            if(curs.count > 0 && curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()){
                Log.d("DB debug", "First line in table: ${curs.getString(curs.getColumnIndex("name"))}")
                curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS WHERE dayOfWeek=6", null)
                curs.moveToFirst()
                if(curs.count > 0 && curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()){
                    Log.d("DB debug", "First line in table: ${curs.getString(curs.getColumnIndex("name"))}")
                    curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS WHERE dayOfWeek=7", null)
                    curs.moveToFirst()
                    if(curs.count > 0 && curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()){
                        Log.d("DB debug", "First line in table: ${curs.getString(curs.getColumnIndex("name"))}")
                        curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS WHERE dayOfWeek=1", null)
                        curs.moveToFirst()
                        if(curs.count > 0 && curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()){
                            Log.d("DB debug", "First line in table: ${curs.getString(curs.getColumnIndex("name"))}")
                            Log.d("Check without error", "No errors!")
                            curs.close()
                            return true
                        }
                        else{
                            Log.e("Check error", "Error in sixth if block in check!")
                            curs.close()
                            return false
                        }
                    }
                    else{
                        Log.e("Check error", "Error in forth if block in check!")
                        curs.close()
                        return false
                    }
                }
                else{
                    Log.e("Check error", "Error in third if block in check!")
                    curs.close()
                    return false
                }
            }
            else{
                Log.e("Check error", "Error in second if block in check!")
                curs.close()
                return false
            }
        }
        else{
            Log.e("Check error", "Error in first if block in check!")
            curs.close()
            return false
        }
    }

    /**Метод, вызываемый если проверка "выявила" старого пользователя*/
    private fun onOldUser() {
        val arrayList = ArrayList<View>()
        arrayList.add(findViewById(R.id.add_lesson_olduser_button)) //0
        arrayList.add(findViewById(R.id.lesson_time_edittext)) //1
        arrayList.add(findViewById(R.id.lesson_date_edittext)) //2
        arrayList.add(findViewById(R.id.order_lesson_editText)) //3
        arrayList.add(findViewById(R.id.lesson_name_edittext)) //4
        arrayList.add(findViewById(R.id.lesson_teacher_edittext)) //5
        arrayList.add(findViewById(R.id.ratingBar)) //6
        arrayList.add(findViewById(R.id.ratingBarTwo)) //7
        arrayList.add(findViewById(R.id.homework_edittext)) //8
        arrayList.add(findViewById(R.id.select_color_image)) //9

        /**Объект pagerView*/
        val pagerView = findViewById<ViewPager>(R.id.viewPager)
        pagerView.adapter = PagerAdapter(applicationContext, supportFragmentManager, arrayList)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(pagerView)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
    }
}

/**Адаптер для pagerView*/
class PagerAdapter (contextOb: Context, fm: FragmentManager, objects: ArrayList<View>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val myCon = contextOb
    private val obj = objects
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TodayFragment(myCon, "today", obj)
            1 -> TodayFragment(myCon, "tomorrow", obj)
            2 -> WeekFragment()
            3 -> MonthFragment()
            else -> throw RuntimeException("Error in creating fragments!")
        }

    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> myCon.resources.getString(R.string.today_text)
            1 -> myCon.resources.getString(R.string.tomorrow_text)
            2 -> myCon.resources.getString(R.string.week_text)
            3 -> myCon.resources.getString(R.string.month_text)
            else -> throw RuntimeException("Error in matching text!")
        }
    }

}