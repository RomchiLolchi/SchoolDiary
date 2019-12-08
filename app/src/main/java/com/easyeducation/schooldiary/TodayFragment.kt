package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class TodayFragment(contextGet: Context, mode: String) : Fragment() {

    private val myMode = mode
    /**Переменная контекста*/
    private val context1: Context = contextGet

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /**Переменная arrayList'а с уроками*/
        lateinit var lessonsArray: ArrayList<Lesson>
        /**Переменная view*/
        val view = inflater.inflate(R.layout.fragment_today, container, false)

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
        val num = cal1.get(Calendar.DAY_OF_MONTH)
        month = if (num in 0..11) {
            months[num]
        } else{
            "ERROR"
        }

        val dof: Array<String> = dfs.weekdays
        lateinit var dofOut: String
        val num1 = cal1.get(Calendar.DAY_OF_WEEK)
        dofOut = if (num1 in 0..6) {
            dof[num1]
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
                    Log.d(
                        "dayOfWeek check",
                        "Day of week is: $dayOfWeek1 and it's WEDNESDAY block!"
                    )
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
                    Log.d("dayOfWeek check", "Day of week is: $dayOfWeek1 and it's ERROR block!")
                }
            }
            //TODO Настроить программно height карточки и сделать space между ними
            /**Объект recyclerView*/
            val recyclerView: RecyclerView = view?.findViewById(R.id.recycler_view)!!
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(context1)
            recyclerView.adapter = LessonsAdapter(context1, lessonsArray)
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

    /**Метод получения данных уроков и записи готовых объектов Lesson в ArrayList'ы*/
    private fun getAndInitData(dayOfWeek: Int) : ArrayList<Lesson> {
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
}

/**Адаптер для recyclerView*/
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

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /**Переменная для корректного открытия и закрытия cardView*/
        var isOpen = false
        /**Длинна, добавляющаяся к ConstraintLayout*/
        val additionHeight = 1050
        /**Стандартная длина*/
        val standardHeight = 215

        holder.colorCard.setImageDrawable(context.resources.getDrawable(R.drawable.white_border))
        holder.layoutCardView.layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, standardHeight)

        val secondPart = StringBuilder(array[position].time)
        secondPart.delete(0, 3)
        if(secondPart.length == 1){
            secondPart.insert(0, "0")
            if(secondPart.toString().toInt() > 60){
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
        }
        else{
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
            if (isOpen) {
                //Карточка урока открыта (нужно закрыть)
                isOpen = false
                holder.layoutCardView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    holder.layoutCardView.height - additionHeight
                )
            } else {
                //Карточка урока закрыта (нужно открыть)
                isOpen = true
                holder.layoutCardView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    holder.layoutCardView.height + additionHeight
                )
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


        val layoutParams = holder.layoutCardView.layoutParams
        layoutParams.height = layoutParams.height + 20
        holder.layoutCardView.layoutParams = layoutParams

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

        init {
            timeOfLesson = view1.findViewById(R.id.time_of_lesson_textview)
            minutesOfLesson = view1.findViewById(R.id.minutes_of_lesson_textview)
            nameOfLesson = view1.findViewById(R.id.name_of_lesson_textview)
            colorCard = view1.findViewById(R.id.color_card_imageView)
            layoutCardView = view1.findViewById(R.id.main_layout_of_card_view)
            teacher = view1.findViewById(R.id.teacher_of_lesson_textView)
            homework = view1.findViewById(R.id.homework_textView)
            ratingBarOne = view1.findViewById(R.id.ratingBar1)
            ratingBarTwo = view1.findViewById(R.id.ratingBar2)
        }
    }
}
