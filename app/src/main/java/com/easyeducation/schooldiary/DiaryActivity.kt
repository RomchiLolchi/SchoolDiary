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
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.fragment_diary.*
import okhttp3.OkHttpClient
import java.util.*
import kotlin.collections.ArrayList


/**
 * Главный фрагмент дневника
 */
class DiaryActivity : AppCompatActivity() {

    /**Переменная layout'а создания урока*/
    lateinit var createLessonLayout: ConstraintLayout
    /**Переменная layout'а нового пользователя*/
    lateinit var newUserLayout: ConstraintLayout
    /**Переменная layout'а старого пользователя*/
    lateinit var oldUserLayout: ConstraintLayout
    /**Переменная, хранящая значение количества ratingBar'ов*/
    var ratingBarsAmount = 1
    /**Переменная, хранящая цвет карточек*/
    var color = "white"
    /**Переменная, хранящая значение даты для карточки*/
    var date = "date"
    /**Переменная, хранящая значение времени для карточки*/
    var time = "time"
    /**Переменная, хранящая readable БД*/
    lateinit var readableDB: SQLiteDatabase
    /**Переменная, хранящая writable БД*/
    lateinit var writableDB: SQLiteDatabase
    /**2-ой ratingBar*/
    private lateinit var ratingBar2: RatingBar
    //Переменные для метода prepareButtons
    private lateinit var selColor: ImageView
    /**Переменные даты и времени*/
    private var day = 0
    private var month = 0
    private var year1 = 0
    private var minutes = 0
    private var hours = 0
    private var lessonDayOfWeek = 2
    /**Переменные для показа DatePickerDialog и TimePickerDialog*/
    private lateinit var calendar: Calendar
    var dayMonth = 0
    var dayOfWeek1 = 0
    private var date1 = 15
    private var month1 = 7
    var year2 = 2005
    var hours1 = 12
    var minutes1 = 0

    companion object{
        /**Helper БД*/
        @JvmStatic
        public lateinit var helper: DBHelper

        @JvmStatic
        fun createHelper(context: Context) {
            helper = DBHelper(context, "DiaryDB", null, 4)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_diary)
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

        //applicationContext.deleteDatabase("DiaryDB")

        createHelper(applicationContext)
        readableDB = helper.readableDatabase
        writableDB = helper.writableDatabase

        //Убрать блок комментариев, когда нужно стереть данные из SQLite
        /*writableDB.execSQL("delete from LESSONS_CARDS")
        writableDB.execSQL("delete from TIMETABLE")*/

        //writableDB.delete("LESSONS_CARDS", "_id = ?", arrayOf("18"))

        Stetho.initializeWithDefaults(this)
        OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        ratingBar2 = findViewById(R.id.ratingBar2)
        ratingBar2.isClickable = false
        ratingBar2.visibility = View.INVISIBLE

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
        prepareButtons(false)
    }

    override fun onBackPressed() {
        if(!isOldUser()) {
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
            lesson_date_edittext.text = resources.getText(R.string.choose_date_text)
            lesson_time_edittext.text = resources.getText(R.string.choose_time_text)
            order_lesson_editText.text = SpannableStringBuilder("")
            homework_edittext.text = SpannableStringBuilder("")
            lesson_name_edittext.text = SpannableStringBuilder("")
            lesson_teacher_edittext.text = SpannableStringBuilder("")
            color = "white"
        }
        else {
            val newLesson = findViewById<ImageView>(R.id.new_lesson_button)
            newLesson.setOnClickListener {
                setVisParams(true, createLessonLayout)
                main_layout.isClickable = false
                prepareButtons(true)
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
                Toast.makeText(applicationContext, "Скоро будет...", Toast.LENGTH_SHORT).show()
                /*val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)*/
            }

            val deleteAttach = findViewById<ImageView>(R.id.delete_attachment_imageview)
            deleteAttach.setOnClickListener {
                Toast.makeText(applicationContext, "Скоро будет...", Toast.LENGTH_SHORT).show()
                /*val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)*/
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
                    DatePickerDialog(this@DiaryActivity, myCallBack, year2, month1, date1).show()
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
                    minutes = minute
                    time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(hourOfDay.toString().length == 1){
                            "0${view.hour}:${view.minute}"
                        }
                        else {
                            "${view.hour}:${view.minute}"
                        }
                    } else {
                        if(hourOfDay.toString().length == 1) {
                            "0$hourOfDay:$minute"
                        }
                        else{
                            "$hourOfDay:$minute"
                        }
                    }
                    lesson_time_edittext.text = SpannableStringBuilder("${resources.getText(R.string.time_text)} $time")
                }
                createLessonLayout.post {
                    TimePickerDialog(this@DiaryActivity, myCallBack, hours1, minutes1, false).show()
                }
            }

            val addLesson = findViewById<Button>(R.id.add_lesson_button)
            addLesson.setOnClickListener {
                val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)

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
                cal.set(Calendar.MINUTE, minutes)
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

                val values1 = ContentValues()
                values1.put("name", lesson_name_edittext.text.toString())
                values1.put("lessonsOrder", order_lesson_editText.text.toString())
                values1.put("dayOfWeek", lessonDayOfWeek)

                writableDB.insertOrThrow("TIMETABLE", null, values1)

                Log.d("DB debug", "Data of lesson in content values:\n" +
                        "name: ${values.get("name")}\n" +
                        "color: ${values.get("color")}\n" +
                        "date: ${values.get("date")}\n" +
                        "dayOfWeek: ${values.get("dayOfWeek")}\n" +
                        "time: ${values.get("time")}\n" +
                        "teacher: ${values.get("teacher")}\n" +
                        "order: ${values1.get("lessonsOrder")}\n" +
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
                prepareButtons(true)
                //TODO Сделать так, чтобы при не вводе данных урок не добавлялся
                //TODO Закрыть БД после использования
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
        if(curs.count > 0 && curs.getString(curs.getColumnIndex("name")) != null && curs.getString(curs.getColumnIndex("name")).isNotEmpty() && curs.getString(curs.getColumnIndex("name")).isNotBlank()){
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
                            curs = readableDB.rawQuery("SELECT name FROM LESSONS_CARDS WHERE dayOfWeek=2", null)
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
                            Log.e("Check error", "Error in fifth if block in check!")
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
        /**Переменная arrayList'а с уроками*/
        lateinit var lessonsArray: ArrayList<Lesson>

        val cal1 = Calendar.getInstance()
        cal1.set(year2, month1, dayMonth)
        dayOfWeek1 = cal1.get(Calendar.DAY_OF_WEEK)

        when(dayOfWeek1){
            2 -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's MONDAY block!")
            }
            3 -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's TUESDAY block!")
            }
            4 -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's WEDNESDAY block!")
            }
            5 -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's THURSDAY block!")
            }
            6 -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's FRIDAY block!")
            }
            else -> {
                lessonsArray = getAndInitData(dayOfWeek1)
                Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's MONDAY block!")
            }
        }

        /**Переменная объекта recyclerView*/
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = LessonsAdapter(applicationContext, lessonsArray)
        }
    }

    /**Метод получения данных уроков и записи готовых объектов Lesson в ArrayList'ы*/
    public fun getAndInitData(dayOfWeek: Int) : ArrayList<Lesson> {
        val readyArrayList = ArrayList<Lesson>()

        when(dayOfWeek){
            5 -> {
                Log.d("", "")
                val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE dayOfWeek=5 ORDER BY lessonsOrder ASC;", null)
                mondayCurs.moveToFirst()
                for(i in 0 until mondayCurs.count) {
                    if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                        readyArrayList.add(Lesson(name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
                            date = mondayCurs.getString(mondayCurs.getColumnIndex("date")), dayOfWeek = mondayCurs.getString(mondayCurs.getColumnIndex("dayOfWeek")),
                            time = mondayCurs.getString(mondayCurs.getColumnIndex("time")), teachers = mondayCurs.getString(mondayCurs.getColumnIndex("teacher")),
                            ratingOne = mondayCurs.getString(mondayCurs.getColumnIndex("ratingOne")), ratingTwo = mondayCurs.getString(mondayCurs.getColumnIndex("ratingTwo")),
                            homework = mondayCurs.getString(mondayCurs.getColumnIndex("homework")), order = mondayCurs.getString(mondayCurs.getColumnIndex("lessonsOrder"))))
                        mondayCurs.moveToNext()
                    }
                    else {
                       break
                    }
                }
                mondayCurs.close()
            }
            6 -> {
                val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE dayOfWeek=6 ORDER BY lessonsOrder ASC;", null)
                mondayCurs.moveToFirst()
                for(i in 0 until mondayCurs.count) {
                    if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                        readyArrayList.add(Lesson(name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
                            date = mondayCurs.getString(mondayCurs.getColumnIndex("date")), dayOfWeek = mondayCurs.getString(mondayCurs.getColumnIndex("dayOfWeek")),
                            time = mondayCurs.getString(mondayCurs.getColumnIndex("time")), teachers = mondayCurs.getString(mondayCurs.getColumnIndex("teacher")),
                            ratingOne = mondayCurs.getString(mondayCurs.getColumnIndex("ratingOne")), ratingTwo = mondayCurs.getString(mondayCurs.getColumnIndex("ratingTwo")),
                            homework = mondayCurs.getString(mondayCurs.getColumnIndex("homework")), order = mondayCurs.getString(mondayCurs.getColumnIndex("lessonsOrder"))))
                        mondayCurs.moveToNext()
                    }
                    else {
                        break
                    }
                }
                mondayCurs.close()
            }
            7 -> {
                val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE dayOfWeek=7 ORDER BY lessonsOrder ASC;", null)
                mondayCurs.moveToFirst()
                for(i in 0 until mondayCurs.count) {
                    if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                        readyArrayList.add(Lesson(name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
                            date = mondayCurs.getString(mondayCurs.getColumnIndex("date")), dayOfWeek = mondayCurs.getString(mondayCurs.getColumnIndex("dayOfWeek")),
                            time = mondayCurs.getString(mondayCurs.getColumnIndex("time")), teachers = mondayCurs.getString(mondayCurs.getColumnIndex("teacher")),
                            ratingOne = mondayCurs.getString(mondayCurs.getColumnIndex("ratingOne")), ratingTwo = mondayCurs.getString(mondayCurs.getColumnIndex("ratingTwo")),
                            homework = mondayCurs.getString(mondayCurs.getColumnIndex("homework")), order = mondayCurs.getString(mondayCurs.getColumnIndex("lessonsOrder"))))
                        mondayCurs.moveToNext()
                    }
                    else {
                        break
                    }
                }
                mondayCurs.close()
            }
            1 -> {
                val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE dayOfWeek=1 ORDER BY lessonsOrder ASC;", null)
                mondayCurs.moveToFirst()
                for(i in 0 until mondayCurs.count) {
                    if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                        readyArrayList.add(Lesson(name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
                            date = mondayCurs.getString(mondayCurs.getColumnIndex("date")), dayOfWeek = mondayCurs.getString(mondayCurs.getColumnIndex("dayOfWeek")),
                            time = mondayCurs.getString(mondayCurs.getColumnIndex("time")), teachers = mondayCurs.getString(mondayCurs.getColumnIndex("teacher")),
                            ratingOne = mondayCurs.getString(mondayCurs.getColumnIndex("ratingOne")), ratingTwo = mondayCurs.getString(mondayCurs.getColumnIndex("ratingTwo")),
                            homework = mondayCurs.getString(mondayCurs.getColumnIndex("homework")), order = mondayCurs.getString(mondayCurs.getColumnIndex("lessonsOrder"))))
                        mondayCurs.moveToNext()
                    }
                    else {
                        break
                    }
                }
                mondayCurs.close()
            }
            2 -> {
                val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE dayOfWeek=2 ORDER BY lessonsOrder ASC;", null)
                mondayCurs.moveToFirst()
                for(i in 0 until mondayCurs.count) {
                    if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                        readyArrayList.add(Lesson(name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
                            date = mondayCurs.getString(mondayCurs.getColumnIndex("date")), dayOfWeek = mondayCurs.getString(mondayCurs.getColumnIndex("dayOfWeek")),
                            time = mondayCurs.getString(mondayCurs.getColumnIndex("time")), teachers = mondayCurs.getString(mondayCurs.getColumnIndex("teacher")),
                            ratingOne = mondayCurs.getString(mondayCurs.getColumnIndex("ratingOne")), ratingTwo = mondayCurs.getString(mondayCurs.getColumnIndex("ratingTwo")),
                            homework = mondayCurs.getString(mondayCurs.getColumnIndex("homework")), order = mondayCurs.getString(mondayCurs.getColumnIndex("lessonsOrder"))))
                        mondayCurs.moveToNext()
                    }
                    else {
                        break
                    }
                }
                mondayCurs.close()
            }
        }

        return readyArrayList
    }

}

/**Адаптер для listView*/
class LessonsAdapter(contextO: Context, arrayO: ArrayList<Lesson>) : RecyclerView.Adapter<LessonsAdapter.ViewHolder>(){

    private var context: Context = contextO
    private var array: ArrayList<Lesson> = arrayO
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val view = layoutInflater.inflate(R.layout.card_view_layout, null)
    lateinit var backgroundColor: String
    //TODO Сделать получение длительности урока из настроек
    /**Переменная длительности урока*/
    val minutesOfLesson = 43

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_border))
        if(holder == null){
            holder.nameOfLesson = view.findViewById(R.id.name_of_lesson_textview)
            holder.minutesOfLesson = view.findViewById(R.id.minutes_of_lesson_textview)
            holder.timeOfLesson = view.findViewById(R.id.time_of_lesson_textview)
            val cursor = DiaryActivity.helper.writableDatabase.rawQuery("SELECT color FROM LESSONS_CARDS", null)
            view.tag = holder
            holder.nameOfLesson.text = array[position].name

            val secondPart = StringBuilder(array[position].time)
            if(secondPart.length == 4){
                secondPart.delete(2, 3)
            }
            else{
                secondPart.delete(1, 4)
            }
            val firstPart = StringBuilder(array[position].time)
            firstPart.delete(2, 5)
            if(secondPart.toString().toInt() + 45 >= 60){
                if(((secondPart.toString().toInt() + 45) - 60).toString().length == 1){
                    val stringBuild = StringBuilder(((secondPart.toString().toInt() + 45) - 60).toString())
                    holder.timeOfLesson.text = "${array[position].time}-${(firstPart.toString().toInt() + 1)}:0$stringBuild"
                }
                else {
                    holder.timeOfLesson.text = "${array[position].time}-${(firstPart.toString().toInt() + 1)}:${((secondPart.toString().toInt() + 45) - 60)}"
                }
            }
            else{
                holder.timeOfLesson.text = "${array[position].time}-$firstPart:${secondPart.toString().toInt() + 45}"
            }
            holder.nameOfLesson.text = array[position].name
            holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
            val cursorColor = DiaryActivity.helper.readableDatabase.rawQuery("SELECT color FROM LESSONS_CARDS WHERE name='${array[position].name}' AND lessonsOrder='${array[position].order}' AND dayOfWeek='${array[position].dayOfWeek}' AND date='${array[position].date}' AND time='${array[position].time}';", null)
            if(cursorColor.count > 0) {
                cursorColor.moveToFirst()
                when (cursorColor.getString(cursorColor.getColumnIndex("color"))) {
                    "white" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                        }
                        view.setBackgroundColor(Color.parseColor("#ffffff"))
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                    "red" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#f58383"))
                        view.setBackgroundColor(Color.parseColor("#f58383"))
                    }
                    "orange" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#fec84c"))
                        view.setBackgroundColor(Color.parseColor("#fec84c"))
                    }
                    "yellow" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#fffa6d"))
                        view.setBackgroundColor(Color.parseColor("#fffa6d"))
                    }
                    "green" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#b0e67"))
                        view.setBackgroundColor(Color.parseColor("#b0e67"))
                    }
                    "blue" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#8bdbff"))
                        view.setBackgroundColor(Color.parseColor("#8bdbff"))
                    }
                    "pink" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffb4c0"))
                        view.setBackgroundColor(Color.parseColor("#ffb4c0"))
                    }
                    "violet" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#c7a4ff"))
                        view.setBackgroundColor(Color.parseColor("#c7a4ff"))
                    }
                }
            }
            else{
                Log.e("DB color error", "Error in first getting if!")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect, null))
                }
                else{
                    holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                }
                view.setBackgroundColor(Color.parseColor("#ffffff"))
            }
            cursorColor.close()
            cursor.close()
        }
        else{
            val secondPart = StringBuilder(array[position].time)
            if(secondPart.length == 4){
                secondPart.delete(0, 3)
            }
            else{
                secondPart.delete(0, 3)
            }
            val firstPart = StringBuilder(array[position].time)
            firstPart.delete(2, 5)
            if(secondPart.toString().toInt() + 45 >= 60){
                if(secondPart.toString().toInt() + 45 >= 60){
                    if(((secondPart.toString().toInt() + 45) - 60).toString().length == 1){
                        val stringBuild = StringBuilder(((secondPart.toString().toInt() + 45) - 60).toString())
                        holder.timeOfLesson.text = "${array[position].time}-${(firstPart.toString().toInt() + 1)}:0$stringBuild"
                    }
                    else {
                        holder.timeOfLesson.text = "${array[position].time}-${(firstPart.toString().toInt() + 1)}:${((secondPart.toString().toInt() + 45) - 60)}"
                    }
                }
                else{
                    holder.timeOfLesson.text = "${array[position].time}-$firstPart:${secondPart.toString().toInt() + 45}"
                }
            }
            holder.nameOfLesson.text = array[position].name
            holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
            holder.timeOfLesson.text = "${array[position].time}-$firstPart:$secondPart"

            val cursorColor = DiaryActivity.helper.readableDatabase.rawQuery("SELECT color FROM LESSONS_CARDS WHERE name='${array[position].name}' AND lessonsOrder='${array[position].order}' AND dayOfWeek='${array[position].dayOfWeek}' AND date='${array[position].date}' AND time='${array[position].time}';", null)
            if(cursorColor.count > 0) {
                cursorColor.moveToFirst()
                when (cursorColor.getString(cursorColor.getColumnIndex("color"))) {
                    "white" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                        }
                        view.setBackgroundColor(Color.parseColor("#ffffff"))
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                    "red" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#f58383"))
                        view.setBackgroundColor(Color.parseColor("#f58383"))
                    }
                    "orange" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#fec84c"))
                        view.setBackgroundColor(Color.parseColor("#fec84c"))
                    }
                    "yellow" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#fffa6d"))
                        view.setBackgroundColor(Color.parseColor("#fffa6d"))
                    }
                    "green" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#b0e67"))
                        view.setBackgroundColor(Color.parseColor("#b0e67"))
                    }
                    "blue" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#8bdbff"))
                        view.setBackgroundColor(Color.parseColor("#8bdbff"))
                    }
                    "pink" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffb4c0"))
                        view.setBackgroundColor(Color.parseColor("#ffb4c0"))
                    }
                    "violet" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect, null))
                        }
                        else{
                            holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect))
                        }
                        holder.layoutCardView.setBackgroundColor(Color.parseColor("#c7a4ff"))
                        view.setBackgroundColor(Color.parseColor("#c7a4ff"))
                    }
                }
            }
            else{
                Log.e("DB color error", "Error in first getting if!")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect, null))
                }
                else{
                    holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                }
                view.setBackgroundColor(Color.parseColor("#ffffff"))
            }
            cursorColor.close()

            if(Locale.getDefault().language == Locale("ru").language) {
                if(minutesOfLesson.toString().length >= 2) {
                    if (minutesOfLesson % 10 == 1) {
                        holder.minutesOfLesson.text =
                            "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}а"
                    } else if (minutesOfLesson % 10 == 2 || minutesOfLesson % 10 == 3 || minutesOfLesson % 10 == 4) {
                        holder.minutesOfLesson.text =
                            "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}ы"
                    } else {
                        holder.minutesOfLesson.text =
                            "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
                    }
                }
                else{
                    if (minutesOfLesson == 1) {
                        holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}а"
                    } else if (minutesOfLesson == 2 || minutesOfLesson == 3 || minutesOfLesson == 4) {
                        holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}ы"
                    } else {
                        holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
                    }
                }
            }
            else{
                if(minutesOfLesson > 1) {
                    holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}s"
                }
                else{
                    holder.minutesOfLesson.text = "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
                }
            }
        }
    }

    class ViewHolder(view1: View) : RecyclerView.ViewHolder(view1) {
        var timeOfLesson: TextView
        var minutesOfLesson: TextView
        var nameOfLesson: TextView
        var colorCard: ImageView
        var layoutCardView: ConstraintLayout

        init {
            timeOfLesson = view1.findViewById(R.id.time_of_lesson_textview)
            minutesOfLesson = view1.findViewById(R.id.minutes_of_lesson_textview)
            nameOfLesson = view1.findViewById(R.id.name_of_lesson_textview)
            colorCard = view1.findViewById(R.id.color_card_imageView)
            layoutCardView = view1.findViewById(R.id.main_layout_of_card_view)
        }
    }
    //override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
}