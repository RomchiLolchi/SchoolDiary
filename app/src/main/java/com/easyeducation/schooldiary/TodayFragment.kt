package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easyeducation.schooldiary.DiaryActivity.Companion.addLesson
import com.easyeducation.schooldiary.DiaryActivity.Companion.date
import com.easyeducation.schooldiary.DiaryActivity.Companion.dayMonth
import com.easyeducation.schooldiary.DiaryActivity.Companion.dayOfWeek1
import com.easyeducation.schooldiary.DiaryActivity.Companion.month1
import com.easyeducation.schooldiary.DiaryActivity.Companion.time
import com.easyeducation.schooldiary.DiaryActivity.Companion.year2
import com.easyeducation.schooldiary.EnterActivity.Companion.writableDB
import kotlinx.android.synthetic.main.card_view_layout.view.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class TodayFragment(contextGet: Context, mode: String, objects: ArrayList<View>) : Fragment() {

    companion object{
        /**Метод получения данных уроков и записи готовых объектов Lesson в ArrayList'ы*/
        @JvmStatic
        fun getAndInitData(date: String) : ArrayList<Lesson> {
            val readyArrayList = ArrayList<Lesson>()

            val mondayCurs = writableDB.rawQuery("SELECT * FROM LESSONS_CARDS WHERE date='$date' ORDER BY lessonsOrder ASC;", null)
            mondayCurs.moveToFirst()
            for(i in 0 until mondayCurs.count) {
                if (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("name")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("name")).isNotBlank()) {
                    readyArrayList.add(Lesson(id = mondayCurs.getInt(mondayCurs.getColumnIndex("_id")), name = mondayCurs.getString(mondayCurs.getColumnIndex("name")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color")),
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

            return readyArrayList
        }

        /**Объект recyclerView*/
        @JvmStatic
        var recyclerView: RecyclerView? = null
    }

    private val myMode = mode
    /**Переменная контекста*/
    private val context1: Context = contextGet
    private val array = objects

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**Переменная arrayList'а с уроками*/
        lateinit var lessonsArray: ArrayList<Lesson>
        /**Переменная view*/
        val view = inflater.inflate(R.layout.fragment_today, container, false)


        val mainLayout = view.findViewById<ConstraintLayout>(R.id.today_main_layout)
        mainLayout.visibility = View.VISIBLE
        mainLayout.isClickable = true

        val dayTextView = view.findViewById<TextView>(R.id.day_textView)

        val cal1 = Calendar.getInstance()
        cal1.set(year2, month1, dayMonth)

        if(myMode == "tomorrow"){
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            recyclerView = view?.findViewById(R.id.recycler_view)!!
        } else{
            recyclerView = view?.findViewById(R.id.recycler_view2)!!
        }
        recyclerView?.visibility = View.VISIBLE
        recyclerView?.isClickable = true

        dayOfWeek1 = cal1.get(Calendar.DAY_OF_WEEK)

        val dfs = DateFormatSymbols()

        val months: Array<String> = dfs.months
        lateinit var month: String
        val num = cal1.get(Calendar.MONTH)
        month = if (num in 0..11) {
            months[num]
        } else{
            "ERROR"
        }

        lateinit var dofOut: String
        when(cal1.get(Calendar.DAY_OF_WEEK)){
            2 -> {
                dofOut = context1.resources.getText(R.string.monday_text).toString()
            }
            3 -> {
                dofOut = context1.resources.getText(R.string.tuesday_text).toString()
            }
            4 -> {
                dofOut = context1.resources.getText(R.string.wednesday_text).toString()
            }
            5 -> {
                dofOut = context1.resources.getText(R.string.thursday_text).toString()
            }
            6 -> {
                dofOut = context1.resources.getText(R.string.friday_text).toString()
            }
            7 -> {
                dofOut = context1.resources.getText(R.string.saturday_text).toString()
            }
            1 -> {
                dofOut = context1.resources.getText(R.string.sunday_text).toString()
            }
            else -> throw RuntimeException("Error in setting day of week")
        }

        dayTextView.text = "$dofOut, ${cal1.get(Calendar.DAY_OF_MONTH)} $month"

        val date = "${cal1.get(Calendar.DAY_OF_MONTH)}.${cal1.get(Calendar.MONTH)}.${cal1.get(Calendar.YEAR)}"
        if(dayOfWeek1 != 1) {
            lessonsArray = getAndInitData(date)

            recyclerView?.setHasFixedSize(true)
            recyclerView?.layoutManager = LinearLayoutManager(context1)
            recyclerView?.adapter =
                recyclerView?.let { LessonsAdapter(context1, lessonsArray, myMode, array, it) }!!
        }
        /*else{
            if(myMode == "today") {
                Toast.makeText(
                    context1,
                    context1.resources.getString(R.string.no_lessons_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/
        return view
    }
}

/**Адаптер для recyclerView*/
class LessonsAdapter(contextO: Context, arrayO: ArrayList<Lesson>, modeVar: String, objects: ArrayList<View>, recycler: RecyclerView) : RecyclerView.Adapter<LessonsAdapter.ViewHolder>() {

    private var mode = modeVar
    private var context: Context = contextO
    private val contextVar = contextO
    private var array: ArrayList<Lesson> = arrayO
    var objects = objects
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val view = layoutInflater.inflate(R.layout.card_view_layout, null)
    //TODO Сделать получение длительности урока из настроек
    /**Переменная длительности урока*/
    val minutesOfLesson = 45
    val recyclerView = recycler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /**Длинна, добавляющаяся к ConstraintLayout*/
        val additionHeight = 1050
        /**Стандартная длина*/
        val standardHeight = 215

        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_border))
        holder.blackoutLayout.layoutParams.height = holder.layoutCardView.layoutParams.height
        holder.blackoutLayout.visibility = View.GONE
        holder.blackoutLayout.isClickable = false
        holder.editCard.visibility = View.GONE
        holder.editCard.isClickable = false
        holder.deleteCard.visibility = View.GONE
        holder.deleteCard.isClickable = false

        view.basic_information_of_card_view.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            standardHeight
        )
        view.information_layout_of_card_view.visibility = View.GONE
        view.information_layout_of_card_view.isClickable = false
        view.main_layout_of_card_view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        view.invalidate()

        if(array[position] == array.last()){
            holder.space.visibility = View.GONE
            holder.space.isClickable = false
        }

        val secondPart = StringBuilder(array[position].time)
        secondPart.delete(0, 3)
        if (secondPart.length == 1) {
            secondPart.insert(0, "0")
            if (secondPart.toString().toInt() > 60) {
                secondPart.delete(0, 1)
                secondPart.append("0")
            }
        }
        val firstPart = StringBuilder(array[position].time)
        firstPart.delete(2, 5)
        if (secondPart.toString().toInt() + 45 >= 60) {
            if (((secondPart.toString().toInt() + 45) - 60).toString().length == 1) {
                val stringBuild =
                    StringBuilder(((secondPart.toString().toInt() + 45) - 60).toString())
                holder.timeOfLesson.text =
                    "$firstPart:$secondPart-${(firstPart.toString().toInt() + 1)}:0$stringBuild"
            } else {
                holder.timeOfLesson.text =
                    "$firstPart:$secondPart-${(firstPart.toString().toInt() + 1)}:${((secondPart.toString().toInt() + 45) - 60)}"
            }
        } else {
            holder.timeOfLesson.text =
                "$firstPart:$secondPart-$firstPart:${secondPart.toString().toInt() + 45}"
        }
        view.invalidate()
        holder.nameOfLesson.text = array[position].name
        holder.minutesOfLesson.text =
            "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"

        val cursorColor = EnterActivity.helper.readableDatabase.rawQuery(
            "SELECT color FROM LESSONS_CARDS WHERE name='${array[position].name}' AND lessonsOrder='${array[position].order}' AND dayOfWeek='${array[position].dayOfWeek}' AND date='${array[position].date}' AND time='${array[position].time}';",
            null
        )
        if (cursorColor.count > 0) {
            cursorColor.moveToFirst()
            when (cursorColor.getString(cursorColor.getColumnIndex("color"))) {
                "white" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.white_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                    }
                    view.setBackgroundColor(Color.parseColor("#ffffff"))
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                "red" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.red_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#f58383"))
                    view.setBackgroundColor(Color.parseColor("#f58383"))
                }
                "orange" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.orange_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#fec84c"))
                    view.setBackgroundColor(Color.parseColor("#fec84c"))
                }
                "yellow" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.yellow_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#fffa6d"))
                    view.setBackgroundColor(Color.parseColor("#fffa6d"))
                }
                "green" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.green_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#b0ee67"))
                    view.setBackgroundColor(Color.parseColor("#b0ee67"))
                }
                "blue" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.blue_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#8bdbff"))
                    view.setBackgroundColor(Color.parseColor("#8bdbff"))
                }
                "pink" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.pink_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffb4c0"))
                    view.setBackgroundColor(Color.parseColor("#ffb4c0"))
                }
                "violet" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.colorCard.setImageDrawable(
                            context.resources.getDrawable(
                                R.drawable.violet_rect,
                                null
                            )
                        )
                    } else {
                        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect))
                    }
                    holder.layoutCardView.setBackgroundColor(Color.parseColor("#c7a4ff"))
                    view.setBackgroundColor(Color.parseColor("#c7a4ff"))
                }
            }
        } else {
            Log.e("DB color error", "Error in first getting if!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.colorCard.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.white_rect,
                        null
                    )
                )
            } else {
                holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
            }
            view.setBackgroundColor(Color.parseColor("#ffffff"))
        }
        cursorColor.close()

        if (Locale.getDefault().language == Locale("ru").language) {
            if (minutesOfLesson.toString().length >= 2) {
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
            } else {
                if (minutesOfLesson == 1) {
                    holder.minutesOfLesson.text =
                        "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}а"
                } else if (minutesOfLesson == 2 || minutesOfLesson == 3 || minutesOfLesson == 4) {
                    holder.minutesOfLesson.text =
                        "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}ы"
                } else {
                    holder.minutesOfLesson.text =
                        "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
                }
            }
        } else {
            if (minutesOfLesson > 1) {
                holder.minutesOfLesson.text =
                    "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}s"
            } else {
                holder.minutesOfLesson.text =
                    "$minutesOfLesson ${context.resources.getString(R.string.minutes_text)}"
            }
        }


        holder.layoutCardView.setOnClickListener {
            if (!holder.isBlackout) {
                if (holder.isOpen) {
                    //Карточка урока открыта (нужно закрыть)
                    holder.isOpen = false
                    holder.informationLayout.visibility = View.GONE
                    holder.informationLayout.isClickable = false
                } else {
                    //Карточка урока закрыта (нужно открыть)
                    holder.isOpen = true
                    holder.informationLayout.visibility = View.VISIBLE
                    holder.informationLayout.isClickable = true
                    holder.informationLayout.background = holder.layoutCardView.background
                    holder.teacher.text = array[position].teachers
                    holder.homework.text =
                        "${context.resources.getString(R.string.your_homework_text)} ${array[position].homework}"
                    holder.ratingBarOne.rating = array[position].ratingOne.toFloat()
                    holder.ratingBarTwo.rating = array[position].ratingTwo.toFloat()
                    holder.ratingBarOne.isClickable = false
                    holder.ratingBarTwo.isClickable = false
                    view.invalidate()
                }
            } else {
                holder.blackoutLayout.visibility = View.GONE
                holder.blackoutLayout.isClickable = false
                holder.editCard.visibility = View.GONE
                holder.editCard.isClickable = false
                holder.deleteCard.visibility = View.GONE
                holder.deleteCard.isClickable = false
            }
        }

        holder.blackoutLayout.setOnClickListener {
            if(!holder.isBlackout){
                //Затемнение есть
                holder.blackoutLayout.visibility = View.GONE
                holder.blackoutLayout.isClickable = false
                holder.editCard.visibility = View.GONE
                holder.editCard.isClickable = false
                holder.deleteCard.visibility = View.GONE
                holder.deleteCard.isClickable = false
            }
        }
        holder.informationLayout.setOnClickListener {
                if (holder.isOpen) {
                    //Карточка урока открыта (нужно закрыть)
                    holder.isOpen = false
                    holder.informationLayout.visibility = View.GONE
                    holder.informationLayout.isClickable = false
                } else {
                    //Карточка урока закрыта (нужно открыть)
                    holder.isOpen = true
                    holder.informationLayout.visibility = View.VISIBLE
                    holder.informationLayout.isClickable = true
                    holder.informationLayout.background = holder.layoutCardView.background
                    holder.teacher.text = array[position].teachers
                    holder.homework.text =
                        "${context.resources.getString(R.string.your_homework_text)} ${array[position].homework}"
                    holder.ratingBarOne.rating = array[position].ratingOne.toFloat()
                    holder.ratingBarTwo.rating = array[position].ratingTwo.toFloat()
                    holder.ratingBarOne.isClickable = false
                    holder.ratingBarTwo.isClickable = false
                    view.invalidate()
                }
            }

        holder.blackoutLayout.setOnLongClickListener {
                if(holder.isOpen){
                    holder.informationLayout.visibility = View.GONE
                    holder.informationLayout.isClickable = false
                }
                if (!holder.isBlackout) {
                    //Затемнения нет (нужно включить)
                    if (holder.informationLayout.visibility == View.VISIBLE) {
                        holder.blackoutLayout.visibility = View.VISIBLE
                        holder.blackoutLayout.isClickable = true
                        holder.editCard.visibility = View.VISIBLE
                        holder.editCard.isClickable = true
                        holder.deleteCard.visibility = View.VISIBLE
                        holder.deleteCard.isClickable = true

                        holder.informationLayout.isClickable = false
                    } else {
                        holder.blackoutLayout.visibility = View.VISIBLE
                        holder.blackoutLayout.isClickable = true
                        holder.editCard.visibility = View.VISIBLE
                        holder.editCard.isClickable = true
                        holder.deleteCard.visibility = View.VISIBLE
                        holder.deleteCard.isClickable = true

                        holder.informationLayout.visibility = View.GONE
                        holder.informationLayout.isClickable = false
                    }
                } else {
                    holder.blackoutLayout.visibility = View.GONE
                    holder.blackoutLayout.isClickable = false
                    holder.editCard.visibility = View.GONE
                    holder.editCard.isClickable = false
                    holder.deleteCard.visibility = View.GONE
                    holder.deleteCard.isClickable = false
                }
                return@setOnLongClickListener true
            }
        holder.layoutCardView.setOnLongClickListener {
                if(holder.isOpen){
                    holder.informationLayout.visibility = View.GONE
                    holder.informationLayout.isClickable = false
                }
                if (!holder.isBlackout) {
                    //Затемнения нет (нужно включить)
                    if (holder.informationLayout.visibility == View.VISIBLE) {
                        holder.blackoutLayout.visibility = View.VISIBLE
                        holder.blackoutLayout.isClickable = true
                        holder.editCard.visibility = View.VISIBLE
                        holder.editCard.isClickable = true
                        holder.deleteCard.visibility = View.VISIBLE
                        holder.deleteCard.isClickable = true

                        holder.informationLayout.isClickable = false
                    } else {
                        holder.blackoutLayout.visibility = View.VISIBLE
                        holder.blackoutLayout.isClickable = true
                        holder.editCard.visibility = View.VISIBLE
                        holder.editCard.isClickable = true
                        holder.deleteCard.visibility = View.VISIBLE
                        holder.deleteCard.isClickable = true

                        holder.informationLayout.visibility = View.GONE
                        holder.informationLayout.isClickable = false
                    }
                } else {
                    holder.blackoutLayout.visibility = View.GONE
                    holder.blackoutLayout.isClickable = false
                    holder.editCard.visibility = View.GONE
                    holder.editCard.isClickable = false
                    holder.deleteCard.visibility = View.GONE
                    holder.deleteCard.isClickable = false
                }
                return@setOnLongClickListener true
            }

        holder.editCard.setOnClickListener {
            val layout = DiaryActivity.createLessonLayout

            layout.visibility = View.VISIBLE
            layout.isClickable = true

            DiaryActivity.color = array[position].color//findColor(holder.layoutCardView)

            (objects[1] as TextView).text = "${context.resources.getString(R.string.time_text)} ${array[position].time}"
            (objects[2] as TextView).text = "${context.resources.getString(R.string.date_text)} ${array[position].date}"
            (objects[3] as EditText).text = SpannableStringBuilder(array[position].order)
            (objects[4] as EditText).text = SpannableStringBuilder(array[position].name)
            (objects[5] as EditText).text = SpannableStringBuilder(array[position].teachers)
            (objects[6] as RatingBar).rating = array[position].ratingOne.toFloat()
            (objects[7] as RatingBar).rating = array[position].ratingTwo.toFloat()
            (objects[8] as EditText).text = SpannableStringBuilder(array[position].homework)
            when(DiaryActivity.color) {
                "white" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.white_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.white_border)
                    DiaryActivity.color = "white"
                }
                "red" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.red_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.red_border)
                    DiaryActivity.color = "red"
                }
                "orange" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.orange_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.orange_border)
                    DiaryActivity.color = "orange"
                }
                "yellow" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.yellow_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.yellow_border)
                    DiaryActivity.color = "yellow"
                }
                "green" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.green_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.green_border)
                    DiaryActivity.color = "green"
                }
                "blue" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.blue_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.blue_border)
                    DiaryActivity.color = "blue"
                }
                "pink" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.pink_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.pink_border)
                    DiaryActivity.color = "pink"
                }
                "violet" -> {
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.violet_rect))
                    layout.background = contextVar.resources.getDrawable(R.drawable.violet_border)
                    DiaryActivity.color = "violet"
                }
            }

            addLesson.apply {
                text = contextVar.resources.getString(R.string.update_lesson_text)
                setOnClickListener {
                    holder.layoutCardView.isClickable = false
                    holder.informationLayout.isClickable = false
                    holder.blackoutLayout.isClickable = false
                    objects[0].isClickable = false //Кнопка создания урока

                    val inputManager = contextVar.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                    var isNeedToUpdate = false
                    if((objects[3] as EditText).text.toString() != array[position].order){
                        isNeedToUpdate = true
                    }

                    time = array[position].time
                    date = array[position].date

                    if(time == "time"){
                        time = "12:0"
                    }
                    if(date == "date"){
                        date = "1.0.2019"
                    }
                    if((objects[3] as EditText).text.toString().isNullOrBlank() || (objects[3] as EditText).text.toString().isNullOrEmpty()){ //order_lesson_editText
                        (objects[3] as EditText).text = SpannableStringBuilder("1")
                    }

                    val values = ContentValues()
                    values.put("name", (objects[4] as TextView).text.toString())
                    values.put("color", DiaryActivity.color)
                    values.put("date", date)
                    val cal = Calendar.getInstance()
                    cal.firstDayOfWeek = Calendar.MONDAY
                    cal.minimalDaysInFirstWeek = 4
                    cal.set(Calendar.YEAR, DiaryActivity.year1)
                    cal.set(Calendar.MONTH, DiaryActivity.month)
                    cal.set(Calendar.DAY_OF_MONTH, DiaryActivity.day)
                    if(DiaryActivity.minutes.isNullOrBlank() || DiaryActivity.minutes.isNullOrEmpty()){
                        DiaryActivity.minutes = "0"
                    }
                    cal.set(Calendar.MINUTE, DiaryActivity.minutes.toInt())
                    if(DiaryActivity.hours == null){
                        DiaryActivity.hours = 0
                    }
                    cal.set(Calendar.HOUR_OF_DAY, DiaryActivity.hours)
                    val lessonDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    //Toast.makeText(applicationContext, lessonDayOfWeek, Toast.LENGTH_SHORT).show()
                    values.put("dayOfWeek", lessonDayOfWeek)
                    values.put("time", time)
                    values.put("teacher", (objects[5] as TextView).text.toString())
                    values.put("ratingOne", (objects[6] as RatingBar).rating)
                    values.put("ratingTwo", (objects[7] as RatingBar).rating)
                    values.put("homework", (objects[8] as EditText).text.toString())
                    values.put("lessonsOrder",(objects[3] as EditText).text.toString())
                    writableDB.execSQL(
                        "UPDATE LESSONS_CARDS SET name = '${values.get("name")}', color = '${values.get(
                            "color"
                        )}', date = '${values.get("date")}', dayOfWeek = '${values.get("dayOfWeek")}', time = '${values.get(
                            "time"
                        )}', teacher = '${values.get("teacher")}', ratingOne = '${values.get("ratingOne")}', ratingTwo = '${values.get(
                            "ratingTwo"
                        )}', homework = '${values.get("homework")}', lessonsOrder = '${values.get("lessonsOrder")}' WHERE _id = '${array[position].id}' AND name='${array[position].name}' AND color='${array[position].color}' " +
                                "AND homework='${array[position].homework}' AND teacher='${array[position].teachers}' AND ratingOne='${array[position].ratingOne}'" +
                                " AND ratingTwo='${array[position].ratingTwo}' AND time='${array[position].time}'"
                    )

                    holder.nameOfLesson.text = values.getAsString("name")
                    val second = StringBuilder(array[position].time)
                    second.delete(0, 3)
                    if (second.length == 1) {
                        second.insert(0, "0")
                        if (second.toString().toInt() > 60) {
                            second.delete(0, 1)
                            second.append("0")
                        }
                    }
                    val first = StringBuilder(array[position].time)
                    first.delete(2, 5)
                    if (second.toString().toInt() + 45 >= 60) {
                        if (((second.toString().toInt() + 45) - 60).toString().length == 1) {
                            val stringBuild =
                                StringBuilder(((second.toString().toInt() + 45) - 60).toString())
                            holder.timeOfLesson.text =
                                "$first:$second-${(first.toString().toInt() + 1)}:0$stringBuild"
                        } else {
                            holder.timeOfLesson.text =
                                "$first:$second-${(first.toString().toInt() + 1)}:${((second.toString().toInt() + 45) - 60)}"
                        }
                    } else {
                        holder.timeOfLesson.text =
                            "$first:$second-$first:${second.toString().toInt() + 45}"
                    }
                    holder.teacher.text = values.getAsString("teacher")
                    holder.ratingBarOne.rating = values.getAsFloat("ratingOne")
                    holder.ratingBarTwo.rating = values.getAsFloat("ratingTwo")
                    holder.homework.text = "H/w: ${values.get("homework")}"

                    array[position].name = values.getAsString("name")
                    array[position].color = DiaryActivity.color
                    array[position].teachers = values.getAsString("teacher")
                    array[position].ratingOne = values.getAsString("ratingOne")
                    array[position].ratingTwo = values.getAsString("ratingTwo")
                    array[position].homework = values.getAsString("homework")
                    array[position].order = (objects[3] as EditText).text.toString()
                    array[position].date = date
                    array[position].time = time
                    array[position].dayOfWeek = lessonDayOfWeek.toString()


                    val cal1 = Calendar.getInstance()
                    cal1.set(year2, month1, dayMonth)
                    if(mode == "tomorrow"){
                        cal1.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    layout.visibility = View.INVISIBLE
                    layout.isClickable = false

                    layout.background = contextVar.resources.getDrawable(R.drawable.white_border)
                    (objects[1] as TextView).text = contextVar.resources.getString(R.string.choose_time_text)
                    (objects[2] as TextView).text = contextVar.resources.getString(R.string.choose_date_text)
                    (objects[3] as EditText).text = SpannableStringBuilder("")
                    (objects[9] as ImageView).setImageDrawable(contextVar.resources.getDrawable(R.drawable.white_rect))
                    (objects[6] as RatingBar).rating = 0.0F
                    (objects[7] as RatingBar).rating = 0.0F
                    (objects[4] as EditText).text = SpannableStringBuilder("")
                    (objects[5] as EditText).text = SpannableStringBuilder("")
                    (objects[8] as EditText).text = SpannableStringBuilder("")

                    when(DiaryActivity.color){
                        "white" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorWhite))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorWhite))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.white_rect))
                        }
                        "red" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorRed))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorRed))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.red_rect))
                        }
                        "orange" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorOrange))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorOrange))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.orange_rect))
                        }
                        "yellow" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorYellow))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorYellow))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.yellow_rect))
                        }
                        "green" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorGreen))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorGreen))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.green_rect))
                        }
                        "blue" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorBlue))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorBlue))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.blue_rect))
                        }
                        "pink" -> {
                            holder.layoutCardView.setBackgroundColor(Color.parseColor("#ffb4e0"))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorPink))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.pink_rect))
                        }
                        "violet" -> {
                            holder.layoutCardView.setBackgroundColor(contextVar.resources.getColor(R.color.colorViolet))
                            holder.informationLayout.setBackgroundColor(contextVar.resources.getColor(R.color.colorViolet))
                            holder.colorCard.setImageDrawable(contextVar.resources.getDrawable(R.drawable.violet_rect))
                        }
                    }
                    if(isNeedToUpdate) {
                        recyclerView.adapter = LessonsAdapter(
                            contextVar,
                            TodayFragment.getAndInitData(
                                "${cal1.get(Calendar.DAY_OF_MONTH)}.${cal1.get(
                                    Calendar.MONTH
                                )}.${cal1.get(Calendar.YEAR)}"
                            ),
                            mode,
                            objects,
                            recyclerView
                        )
                    }

                    holder.layoutCardView.isClickable = true
                    holder.informationLayout.isClickable = true
                    holder.blackoutLayout.visibility = View.INVISIBLE
                    holder.blackoutLayout.isClickable = false
                    objects[0].isClickable = true //Кнопка создания урока

                    holder.teacher.postInvalidate()
                    holder.ratingBarOne.postInvalidate()
                    holder.ratingBarTwo.postInvalidate()
                    holder.homework.postInvalidate()
                    holder.layoutCardView.postInvalidate()
                    holder.informationLayout.postInvalidate()
                }
            }
        }

        holder.deleteCard.setOnClickListener {
            var homeworkText = StringBuilder(array[position].homework)
            //homeworkText.delete(0, 4)
            writableDB.execSQL("DELETE FROM LESSONS_CARDS WHERE name='${holder.nameOfLesson.text}' AND color='${findColor(holder.layoutCardView)}' AND homework='$homeworkText' AND teacher='${array[position].teachers}' AND ratingOne=${array[position].ratingOne} AND ratingTwo=${array[position].ratingTwo} AND time='${array[position].time}'")
            val cal1 = Calendar.getInstance()
            cal1.set(year2, month1, dayMonth)
            if(mode == "tomorrow"){
                cal1.add(Calendar.DAY_OF_MONTH, 1)
            }
            val date = "${cal1.get(Calendar.DAY_OF_MONTH)}.${cal1.get(Calendar.MONTH)}.${cal1.get(Calendar.YEAR)}"
            recyclerView.adapter = LessonsAdapter(context, TodayFragment.getAndInitData(date), mode, objects, recyclerView)
            recyclerView.invalidate()
            holder.layoutCardView.invalidate()
            if(holder.informationLayout.visibility == View.VISIBLE){
                holder.informationLayout.invalidate()
            }
            holder.blackoutLayout.visibility = View.INVISIBLE
            holder.blackoutLayout.isClickable = false
        }
    }

        class ViewHolder(view1: View) : RecyclerView.ViewHolder(view1) {
            var timeOfLesson: TextView
            var minutesOfLesson: TextView
            var nameOfLesson: TextView
            var colorCard: ImageView
            var layoutCardView: ConstraintLayout
            var teacher: TextView
            var homework: TextView
            var ratingBarOne: RatingBar
            var ratingBarTwo: RatingBar
            var informationLayout: ConstraintLayout
            /**Переменная для корректной работы "затемнения" карточки*/
            var isBlackout = false
            var editCard: ImageView
            var deleteCard: ImageView
            var blackoutLayout: ConstraintLayout
            /**Переменная для корректного открытия и закрытия cardView*/
            var isOpen = false
            var space: Space

            init {
                timeOfLesson = view1.findViewById(R.id.time_of_lesson_textview)
                minutesOfLesson = view1.findViewById(R.id.minutes_of_lesson_textview)
                nameOfLesson = view1.findViewById(R.id.name_of_lesson_textview)
                colorCard = view1.findViewById(R.id.color_card_imageView)
                layoutCardView = view1.findViewById(R.id.basic_information_of_card_view)
                teacher = view1.findViewById(R.id.teacher_of_lesson_textView)
                homework = view1.findViewById(R.id.homework_textView)
                ratingBarOne = view1.findViewById(R.id.ratingBar1)
                ratingBarTwo = view1.findViewById(R.id.ratingBar2)
                informationLayout = view1.findViewById(R.id.information_layout_of_card_view)
                blackoutLayout = view1.findViewById(R.id.blackout_layout)
                editCard = view1.findViewById(R.id.edit_card_imageView)
                deleteCard = view1.findViewById(R.id.delete_card_imageView)
                space = view1.findViewById(R.id.space)
            }
        }

    @SuppressLint("NewApi")
    private fun findColor(colorRes: ConstraintLayout) : String {
        lateinit var color: String

        val colorBackgr: ColorDrawable = colorRes.background as ColorDrawable

        when (colorBackgr.color) {
            Color.parseColor("#ffffff") -> {
                color = "white"
            }

            Color.parseColor("#f58383") -> {
                color = "red"
            }

            Color.parseColor("#fec84c") -> {
                color = "orange"
            }

            Color.parseColor("#fffa6d") -> {
                color = "yellow"
            }

            Color.parseColor("#b0ee67") -> {
                color = "green"
            }

            Color.parseColor("#8bdbff") -> {
                color = "blue"
            }

            Color.parseColor("#ffb4e0") -> {
                color = "pink"
            }

            Color.parseColor("#c7a4ff") -> {
                color = "violet"
            }

            else -> throw RuntimeException("Color was't found")
        }

        return color
    }
}