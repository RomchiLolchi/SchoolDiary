package com.easyeducation.schooldiary

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?){
        onUpgrade(db, 1, 2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion <= 1){
            db?.execSQL("CREATE TABLE LESSONS_CARDS (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, color TEXT, date TEXT, dayOfWeek TEXT, time TEXT, lessonsOrder TEXT, teacher TEXT, ratingOne TEXT, ratingTwo TEXT, homework TEXT);")


            db?.execSQL("CREATE TABLE TEACHERS(" +
                    //"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name INTEGER);")
        }
        if(newVersion <= 2){
            db?.execSQL("CREATE TABLE TIMETABLE (_id INTEGER PRIMARY KEY, name TEXT, lessonsOrder TEXT, dayOfWeek TEXT);")
        }
    }

}