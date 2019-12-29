package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easyeducation.schooldiary.DiaryActivity.Companion.dayMonth
import com.easyeducation.schooldiary.DiaryActivity.Companion.dayOfWeek1
import com.easyeducation.schooldiary.DiaryActivity.Companion.month1
import com.easyeducation.schooldiary.DiaryActivity.Companion.writableDB
import com.easyeducation.schooldiary.DiaryActivity.Companion.year2
import kotlinx.android.synthetic.main.card_view_layout.view.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class TodayFragment(contextGet: Context, mode: String) : Fragment() {

    companion object{
        /**Метод получения данных уроков и записи готовых объектов Lesson в ArrayList'ы*/
        @JvmStatic
        fun getAndInitData(dayOfWeek: Int) : ArrayList<Lesson> {
            val readyArrayList = ArrayList<Lesson>()

            when(dayOfWeek){
                2 -> {
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
                3 -> {
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
                4 -> {
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
                5 -> {
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
                6 -> {
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

        /**Объект recyclerView*/
        @JvmStatic
        lateinit var recyclerView: RecyclerView
    }

    private val myMode = mode
    /**Переменная контекста*/
    private val context1: Context = contextGet

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**Переменная arrayList'а с уроками*/
        lateinit var lessonsArray: ArrayList<Lesson>
        /**Переменная view*/
        val view = inflater.inflate(R.layout.fragment_today, container, false)

        recyclerView = view?.findViewById(R.id.recycler_view)!!

        val dayTextView = view.findViewById<TextView>(R.id.day_textView)

        val cal1 = Calendar.getInstance()
        cal1.set(year2, month1, dayMonth)
        if(myMode == "tomorrow"){
            cal1.add(Calendar.DAY_OF_MONTH, 1)
        }
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

        val weekDays: Array<String> = dfs.weekdays
        lateinit var dofOut: String
        val num1 = cal1.get(Calendar.DAY_OF_WEEK)
        dofOut = if (num1 in 1..7) {
            weekDays[num1]
        } else{
            "ERROR"
        }

        dayTextView.text = "$dofOut, ${cal1.get(Calendar.DAY_OF_MONTH)} $month"

        if(dayOfWeek1 != 1) {
            when (dayOfWeek1) {
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
                    lessonsArray = ArrayList()
                    Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's ERROR block!")
                }
            }
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(context1)
            recyclerView.adapter = LessonsAdapter(context1, lessonsArray, myMode)
        }
        else{
            if(myMode != "tomorrow") {
                Toast.makeText(
                    context1,
                    context1.resources.getString(R.string.no_lessons_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return view
    }
}

/**Адаптер для recyclerView*/
class LessonsAdapter(contextO: Context, arrayO: ArrayList<Lesson>, modeVar: String) : RecyclerView.Adapter<LessonsAdapter.ViewHolder>() {

    private var mode = modeVar
    private var context: Context = contextO
    public var array: ArrayList<Lesson> = arrayO
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val view = layoutInflater.inflate(R.layout.card_view_layout, null)
    lateinit var backgroundColor: String
    //TODO Сделать получение длительности урока из настроек
    /**Переменная длительности урока*/
    val minutesOfLesson = 45

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

        val cursorColor = DiaryActivity.helper.readableDatabase.rawQuery(
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
            val dayOfWeekLocalVar = cal1.get(Calendar.DAY_OF_WEEK)
            TodayFragment.recyclerView.adapter = LessonsAdapter(context, TodayFragment.getAndInitData(dayOfWeekLocalVar), mode)
        }

        /*val layoutParams = holder.layoutCardView.layoutParams
        layoutParams.height = layoutParams.height + 20
        holder.layoutCardView.layoutParams = layoutParams*/

        //Won't be implemented now
        /*val layoutParamsForSpace =  ViewGroup.LayoutParams//DiaryActivity.recyclerView.layoutParams
        layoutParamsForSpace.height = 3
        val space = Space(context)
        space.setBackgroundColor(Color.GREEN)
        space.layoutParams = layoutParamsForSpace
        DiaryActivity.recyclerView.addView(space)*/
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
            }
        }

    private fun findColor(colorRes: ConstraintLayout) : String{
        lateinit var color: String

        val colorBackgr: ColorDrawable = colorRes.background as ColorDrawable

        when(colorBackgr.color){
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