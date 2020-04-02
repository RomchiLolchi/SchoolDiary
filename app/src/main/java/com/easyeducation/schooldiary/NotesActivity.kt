package com.easyeducation.schooldiary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.easyeducation.schooldiary.NotesActivity.Companion.getAndFillData
import com.easyeducation.schooldiary.NotesActivity.Companion.recyclerView
import com.etsy.android.grid.StaggeredGridView
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.activity_notes.view.*
import kotlinx.android.synthetic.main.note_item_layout.view.*
import java.lang.reflect.Array.get

class NotesActivity : AppCompatActivity() {

    companion object{
        @JvmStatic
        var color: String = "white"
        @JvmStatic
        lateinit var recyclerView: RecyclerView
        @JvmStatic
        lateinit var button: Button
        @JvmStatic
        lateinit var map: MutableMap<String, View>
        @JvmStatic
        lateinit var oldUserLayout: ConstraintLayout
        @JvmStatic
        lateinit var createNoteLayout: ConstraintLayout

        @JvmStatic
        fun isOldUser() : Boolean {
            val curs = EnterActivity.readableDB.rawQuery("SELECT * FROM NOTES", null)
            if(curs.count > 0 && curs.moveToFirst()) {
                if(!curs.getString(curs.getColumnIndex("headline")).isNullOrBlank() || !curs.getString(curs.getColumnIndex("mainText")).isNullOrBlank()) {
                    color = curs.getString(curs.getColumnIndex("color"))
                    curs.close()
                    return true
                }
                else{
                    curs.close()
                    return false
                }
            } else {
                curs.close()
                return false
            }
        }

        @JvmStatic
        private fun getAndFillData(context: Context, recyclerView: RecyclerView, method: () -> Unit) {
            val readyArrayList = ArrayList<Note>()

            val mondayCurs = EnterActivity.writableDB.rawQuery("SELECT * FROM NOTES;", null)
            mondayCurs.moveToFirst()
            for(i in 0 until mondayCurs.count) {
                if ((mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("headline")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("headline")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("headline")).isNotBlank()) || (mondayCurs.count > 0 && mondayCurs.getString(mondayCurs.getColumnIndex("mainText")) != null && mondayCurs.getString(mondayCurs.getColumnIndex("mainText")).isNotEmpty() && mondayCurs.getString(mondayCurs.getColumnIndex("mainText")).isNotBlank())) {
                    readyArrayList.add(Note(headlineIn = mondayCurs.getString(mondayCurs.getColumnIndex("headline")), mainTextIn = mondayCurs.getString(mondayCurs.getColumnIndex("mainText")), timeIn = mondayCurs.getString(mondayCurs.getColumnIndex("time")), dateIn = mondayCurs.getString(mondayCurs.getColumnIndex("date")), color = mondayCurs.getString(mondayCurs.getColumnIndex("color"))))
                    mondayCurs.moveToNext()
                }
                else {
                    break
                }
            }
            mondayCurs.close()

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.adapter = NotesAdapter(context, readyArrayList, map, { context, recyclerView -> getAndFillData(context, recyclerView, method)}, method)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setTheme(R.style.NotesTheme)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        recyclerView = findViewById(R.id.notes_recyclerView)
        button = findViewById(R.id.add_note)
        map = mutableMapOf(Pair("createNoteButton", findViewById(R.id.add_note)), Pair("expandNoteHeadline", findViewById(R.id.note_headline_textView)),
            Pair("expandNoteMainText", findViewById(R.id.note_maintext_textView)), Pair("oldUserLayout", findViewById(R.id.old_notes_user_layout)),
            Pair("createNoteLayout", findViewById(R.id.create_note_layout)), Pair("newNotesUserLayout", findViewById(R.id.new_notes_user_layout)),
            Pair("chooseColor", findViewById(R.id.choose_color_notes)))

        oldUserLayout = findViewById(R.id.old_notes_user_layout)
        createNoteLayout = findViewById(R.id.create_note_layout)

        add_note_olduser_button.setOnClickListener {
            create_note_layout.visibility = View.VISIBLE
            create_note_layout.isClickable = true
            prepareButtons()
        }

        new_note_button.setOnClickListener{
            create_note_layout.visibility = View.VISIBLE
            create_note_layout.isClickable = true
            prepareButtons()
        }

        prepareButtons()
        if(isOldUser()){
            prepareButtons()
            getAndFillData(applicationContext, recyclerView, ::prepareButtons)
            createNoteLayout.visibility = View.INVISIBLE
            createNoteLayout.isClickable = false
            new_notes_user_layout.visibility = View.INVISIBLE
            new_notes_user_layout.isClickable = false
            oldUserLayout.visibility = View.VISIBLE
            oldUserLayout.isClickable = true
        }
        else {
            createNoteLayout.visibility = View.INVISIBLE
            createNoteLayout.isClickable = false
            new_notes_user_layout.visibility = View.VISIBLE
            new_notes_user_layout.isClickable = true
            oldUserLayout.visibility = View.INVISIBLE
            oldUserLayout.isClickable = false
        }
    }

    override fun onBackPressed() {
        if(create_note_layout.visibility == View.VISIBLE){
            create_note_layout.visibility = View.INVISIBLE
            create_note_layout.isClickable = false
        }
        else{
            val intent = Intent(this, EnterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun prepareButtons() {
        color = "white"
        create_note_layout.background = resources.getDrawable(R.drawable.white_border)

        //Подготовка заголовка заметки
        val noteHeading = findViewById<EditText>(R.id.note_headline_textView)
        noteHeading.hint = resources.getText(R.string.notes_heading_text)
        noteHeading.text = SpannableStringBuilder("")

        //Подготовка основного текста заметки
        val noteText = findViewById<EditText>(R.id.note_maintext_textView)
        noteText.hint = resources.getText(R.string.notes_maintext_text)
        noteText.text = SpannableStringBuilder("")

        var isOpen = false
        notes_color_scroll.visibility = View.INVISIBLE
        //Подготовка кнопки выбора цвета
        val selectColor = findViewById<ImageView>(R.id.choose_color_notes)
        selectColor.setImageDrawable(resources.getDrawable(R.drawable.white_rect, null))
        selectColor.setOnClickListener {
            if (isOpen) {
                isOpen = false
                notes_color_scroll.visibility = View.INVISIBLE
            } else {
                isOpen = true
                notes_color_scroll.visibility = View.VISIBLE
            }
        }
        if (!noteHeading.text.isNullOrEmpty() && !noteHeading.text.isNullOrBlank() && !noteText.text.isNullOrEmpty() && !noteText.text.isNullOrBlank()) {
            val colorCurs = EnterActivity.writableDB.rawQuery(
                "SELECT color FROM NOTES WHERE headline=\"${noteHeading.text}\" AND mainText=\"${noteText.text}\"",
                null
            )
            if (colorCurs != null && colorCurs.moveToFirst()) {
                color = colorCurs.getString(colorCurs.getColumnIndex("color"))
            }
            colorCurs.close()
        }

        //Настройка цветов
        notes_white.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.white_rect, null))
            selectColor.callOnClick()
            color = "white"
            create_note_layout.background = resources.getDrawable(R.drawable.white_border, null)
        }
        notes_red.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.red_rect, null))
            selectColor.callOnClick()
            color = "red"
            create_note_layout.background = resources.getDrawable(R.drawable.red_border, null)
        }
        notes_orange.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.orange_rect, null))
            selectColor.callOnClick()
            color = "orange"
            create_note_layout.background = resources.getDrawable(R.drawable.orange_border, null)
        }
        notes_yellow.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.yellow_rect, null))
            selectColor.callOnClick()
            color = "yellow"
            create_note_layout.background = resources.getDrawable(R.drawable.yellow_border, null)
        }
        notes_green.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.green_rect, null))
            selectColor.callOnClick()
            color = "green"
            create_note_layout.background = resources.getDrawable(R.drawable.green_border, null)
        }
        notes_blue.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.blue_rect, null))
            selectColor.callOnClick()
            color = "blue"
            create_note_layout.background = resources.getDrawable(R.drawable.blue_border, null)
        }
        notes_pink.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.pink_rect, null))
            selectColor.callOnClick()
            color = "pink"
            create_note_layout.background = resources.getDrawable(R.drawable.pink_border, null)
        }
        notes_violet.setOnClickListener {
            selectColor.setImageDrawable(resources.getDrawable(R.drawable.violet_rect, null))
            selectColor.callOnClick()
            color = "violet"
            create_note_layout.background = resources.getDrawable(R.drawable.violet_border, null)
        }
        //Настройка нижних кнопок
        notes_add_checklist.setOnClickListener {
            Toast.makeText(applicationContext, R.string.will_be_added_soon, Toast.LENGTH_SHORT)
                .show()
        }
        notes_add_image.setOnClickListener {
            Toast.makeText(applicationContext, R.string.will_be_added_soon, Toast.LENGTH_SHORT)
                .show()
        }
        notes_add_checklist.setOnClickListener {
            Toast.makeText(applicationContext, R.string.will_be_added_soon, Toast.LENGTH_SHORT)
                .show()
        }
        notes_add_audio.setOnClickListener {
            Toast.makeText(applicationContext, R.string.will_be_added_soon, Toast.LENGTH_SHORT)
                .show()
        }
        notes_add_reminder.setOnClickListener {
            Toast.makeText(applicationContext, R.string.will_be_added_soon, Toast.LENGTH_SHORT)
                .show()
        }
        notes_share.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TITLE, noteHeading.text.toString())
                putExtra(Intent.EXTRA_TEXT, noteText.text.toString())
                type = "text/plain"
            }
            val actionIntent =
                Intent.createChooser(sendIntent, resources.getString(R.string.send_note_text))
            startActivity(actionIntent)
        }
        notes_delete.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.apply {
                setTitle(R.string.attention_text)
                setMessage(R.string.are_you_sure_deleting_text)
                setCancelable(true)
                setNegativeButton(R.string.delete_text) { dialogInterface: DialogInterface, i: Int ->
                    EnterActivity.writableDB.execSQL("DELETE FROM NOTES WHERE headline='${noteHeading.text}' AND mainText='${noteText.text}' AND color='$color'")
                    if (isOldUser()) {
                        getAndFillData(applicationContext, recyclerView, ::prepareButtons)
                        create_note_layout.visibility = View.INVISIBLE
                        create_note_layout.isClickable = false
                        new_notes_user_layout.visibility = View.INVISIBLE
                        new_notes_user_layout.isClickable = false
                        old_notes_user_layout.visibility = View.VISIBLE
                        old_notes_user_layout.isClickable = true
                    } else {
                        create_note_layout.visibility = View.INVISIBLE
                        create_note_layout.isClickable = false
                        new_notes_user_layout.visibility = View.VISIBLE
                        new_notes_user_layout.isClickable = true
                        old_notes_user_layout.visibility = View.INVISIBLE
                        old_notes_user_layout.isClickable = false
                    }
                    dialogInterface.cancel()
                }
                setPositiveButton(R.string.keep_text) { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                }
                show()
            }
        }
        new_note_button.visibility = View.VISIBLE
        add_note.setText(R.string.create_note_text)
        add_note.setOnClickListener {
            val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val values = ContentValues()
            values.put("headline", noteHeading.text.toString())
            values.put("mainText", noteText.text.toString())
            values.put("time", "0")
            values.put("date", "0")
            values.put("color", color)
            if((!values.get("headline").toString().isNullOrBlank() || !values.get("headline").toString().isNullOrEmpty()) || (!values.get("mainText").toString().isNullOrBlank() || !values.get("mainText").toString().isNullOrEmpty())) {
                EnterActivity.writableDB.insertOrThrow("NOTES", null, values)
            }
            else {
               Toast.makeText(applicationContext, getText(R.string.alert_of_creating_empty_note), Toast.LENGTH_LONG).show()
            }
            create_note_layout.visibility = View.INVISIBLE
            create_note_layout.isClickable = false

            if (isOldUser()) {
                getAndFillData(applicationContext, recyclerView, ::prepareButtons)
                create_note_layout.visibility = View.INVISIBLE
                create_note_layout.isClickable = false
                new_notes_user_layout.visibility = View.INVISIBLE
                new_notes_user_layout.isClickable = false
                old_notes_user_layout.visibility = View.VISIBLE
                old_notes_user_layout.isClickable = true
            } else {
                create_note_layout.visibility = View.INVISIBLE
                create_note_layout.isClickable = false
                new_notes_user_layout.visibility = View.VISIBLE
                new_notes_user_layout.isClickable = true
                old_notes_user_layout.visibility = View.INVISIBLE
                old_notes_user_layout.isClickable = false
            }
        }
    }
}
/**
 * Адаптер для RecyclerView
 */
class NotesAdapter(var context: Context, var array: ArrayList<Note>, var objects: MutableMap<String, View>, private val dataPrepare: (Context, RecyclerView) -> Unit, private val prepareButtons: () -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.note_item_layout, parent,false)
        objects.putAll(mapOf(Pair("image", v.findViewById<ImageView>(R.id.image_note_item)), Pair("title", v.findViewById<TextView>(R.id.headline_note_item)),
            Pair("mainText", v.findViewById<TextView>(R.id.maintext_note_item)), Pair("noteItemLayout", v.findViewById<LinearLayout>(R.id.note_item_layout))))
        return ViewHolder(v, objects)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteItemLayout.visibility = View.VISIBLE
        holder.noteItemLayout.isClickable = true
        holder.oldUserLayout.visibility = View.VISIBLE
        holder.oldUserLayout.isClickable = true
        holder.createNoteLayout.visibility = View.INVISIBLE
        holder.createNoteLayout.isClickable = false

        val lp = LinearLayout.LayoutParams((EnterActivity.display.width / 2) - 50, LinearLayout.LayoutParams.WRAP_CONTENT)
        holder.noteItemLayout.layoutParams = lp
        holder.mainText.layoutParams = lp
        holder.title.layoutParams = lp

        holder.noteItemLayout.layoutParams = LinearLayout.LayoutParams(holder.noteItemLayout.width - 3, LinearLayout.LayoutParams.WRAP_CONTENT)
        NotesActivity.color = array[position].color

        when(NotesActivity.color){
            "white" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.white_border)
            "red" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.red_border)
            "orange" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.orange_border)
            "yellow" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.yellow_border)
            "green" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.green_border)
            "blue" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.blue_border)
            "pink" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.pink_border)
            "violet" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.violet_border)
        }

        var isHeaderNull = false
        holder.image.visibility = View.GONE
        if(!array[position].headlineIn.isNullOrBlank() || !array[position].headlineIn.isNullOrEmpty()){
            holder.title.visibility = View.VISIBLE
            holder.title.text = array[position].headlineIn
        }
        else{
            holder.title.visibility = View.GONE
            isHeaderNull = true
        }

        var isTextNull = false
        if(!array[position].mainTextIn.isNullOrBlank() || !array[position].mainTextIn.isNullOrEmpty()){
            holder.mainText.visibility = View.VISIBLE
            holder.mainText.text = array[position].mainTextIn
        }
        else{
            holder.mainText.visibility = View.GONE
            isTextNull = true
        }

        holder.noteItemLayout.setOnClickListener {
            NotesActivity.oldUserLayout.isClickable = false
            NotesActivity.createNoteLayout.visibility = View.VISIBLE
            NotesActivity.createNoteLayout.isClickable = true
            recyclerView.isClickable = false

            NotesActivity.color = array[position].color

            when(NotesActivity.color){
                "white" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.white_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.white_rect))
                }
                "red" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.red_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.red_rect))
                }
                "orange" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.orange_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.orange_rect))
                }
                "yellow" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.yellow_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.yellow_rect))
                }
                "green" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.green_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.green_rect))
                }
                "blue" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.blue_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.blue_rect))
                }
                "pink" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.pink_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.pink_rect))
                }
                "violet" -> {
                    holder.createNoteLayout.background = context.resources.getDrawable(R.drawable.violet_border)
                    holder.chooseColor.setImageDrawable(context.resources.getDrawable(R.drawable.violet_rect))
                }
            }

            holder.expandNoteHeadline.hint = context.resources.getString(R.string.notes_heading_text)
            holder.expandNoteHeadline.text = ""
            holder.expandNoteMainText.hint = context.resources.getString(R.string.notes_maintext_text)
            holder.expandNoteMainText.text = ""

            if(!isHeaderNull){
                holder.expandNoteHeadline.text = array[position].headlineIn
            }
            else{
                holder.expandNoteHeadline.hint = context.resources.getString(R.string.notes_heading_text)
            }

            if(!isTextNull){
                holder.expandNoteMainText.text = array[position].mainTextIn
            }
            else{
                holder.expandNoteMainText.hint = context.resources.getString(R.string.notes_maintext_text)
            }

            holder.createNoteButton.text = context.resources.getString(R.string.close_text)
            holder.createNoteButton.setOnClickListener {
                holder.noteItemLayout.visibility = View.VISIBLE
                holder.noteItemLayout.isClickable = true
                NotesActivity.oldUserLayout.visibility = View.VISIBLE
                NotesActivity.oldUserLayout.isClickable = true
                NotesActivity.createNoteLayout.visibility = View.INVISIBLE
                NotesActivity.createNoteLayout.isClickable = false
                recyclerView.isClickable = true

                val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(holder.view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                array[position].color = NotesActivity.color

                if((!holder.expandNoteHeadline.text.toString().isNullOrBlank() || holder.expandNoteHeadline.text.toString().isNullOrEmpty()) || (!holder.expandNoteMainText.text.toString().isNullOrBlank() || !holder.expandNoteMainText.text.toString().isNullOrEmpty())) {

                    //TODO Делать также с датой, временем и т.д
                    array[position].headlineIn = holder.expandNoteHeadline.text.toString()
                    EnterActivity.writableDB.execSQL("UPDATE NOTES SET headline=\"${holder.expandNoteHeadline.text}\" WHERE mainText=\"${holder.mainText.text}'\" AND color=\"${array[position].color}\";")

                    array[position].mainTextIn = holder.expandNoteMainText.text.toString()
                    EnterActivity.writableDB.execSQL("UPDATE NOTES SET mainText=\"${holder.expandNoteMainText.text}\" WHERE headline=\"${holder.expandNoteHeadline.text}\" AND color=\"${array[position].color}\";")

                    array[position].color = NotesActivity.color
                    EnterActivity.writableDB.execSQL("UPDATE NOTES SET color=\"${NotesActivity.color}\" WHERE mainText=\"${holder.expandNoteMainText.text}\" AND headline=\"${holder.expandNoteHeadline.text}\";")
                }
                else{
                    EnterActivity.writableDB.execSQL("DELETE NOTES WHERE headline=\"${holder.expandNoteHeadline.text}\" AND color=\"${array[position].color}\" AND mainText=\"${holder.expandNoteMainText.text}\";")
                    Toast.makeText(context, context.getText(R.string.alert_of_creating_empty_note), Toast.LENGTH_LONG).show()
                }
                holder.title.text = holder.expandNoteHeadline.text.toString()
                holder.mainText.text = holder.expandNoteMainText.text.toString()

                holder.noteItemLayout.invalidate()
                holder.title.invalidate()
                holder.mainText.invalidate()

                dataPrepare

                when(NotesActivity.color){
                    "white" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.white_border)
                    "red" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.red_border)
                    "orange" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.orange_border)
                    "yellow" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.yellow_border)
                    "green" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.green_border)
                    "blue" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.blue_border)
                    "pink" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.pink_border)
                    "violet" -> holder.noteItemLayout.background = context.resources.getDrawable(R.drawable.violet_border)
                }

                holder.noteItemLayout.invalidate()
                holder.title.invalidate()
                holder.mainText.invalidate()
                recyclerView.invalidate()

                holder.createNoteButton.text = context.resources.getString(R.string.create_note_text)
                prepareButtons
                /*val intent = Intent(context, NotesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)*/
            }
        }
    }

    class ViewHolder(val view: View, objects: MutableMap<String, View>) : RecyclerView.ViewHolder(view){
        val image: ImageView = objects["image"] as ImageView //view.findViewById(R.id.image_note_item)
        val title: TextView = objects["title"] as TextView // view.findViewById(R.id.headline_note_item)
        val mainText: TextView = objects["mainText"] as TextView //view.findViewById(R.id.maintext_note_item)
        val noteItemLayout: LinearLayout = objects["noteItemLayout"] as LinearLayout // view.findViewById(R.id.note_item_layout)
        val createNoteButton: Button = objects["createNoteButton"] as Button //view.findViewById(R.id.add_note)
        val expandNoteHeadline: TextView = objects["expandNoteHeadline"] as TextView //view.findViewById(R.id.note_headline_textView)
        val expandNoteMainText: TextView = objects["expandNoteMainText"] as TextView //view.findViewById(R.id.note_maintext_textView)
        val oldUserLayout: ConstraintLayout = objects["oldUserLayout"] as ConstraintLayout //view.findViewById(R.id.old_notes_user_layout)
        val createNoteLayout: ConstraintLayout = objects["createNoteLayout"] as ConstraintLayout //view.findViewById(R.id.create_note_layout)
        val newNotesUserLayout: ConstraintLayout = objects["newNotesUserLayout"] as ConstraintLayout //view.findViewById(R.id.new_notes_user_layout)
        var chooseColor: ImageView = objects["chooseColor"] as ImageView
    }
}