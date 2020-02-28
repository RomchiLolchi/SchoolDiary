package com.easyeducation.schooldiary

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?){
        onUpgrade(db, 4, 5)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 1){
            db?.execSQL("CREATE TABLE LESSONS_CARDS (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, color TEXT, date TEXT, dayOfWeek TEXT, time TEXT, teacher TEXT, ratingOne TEXT, ratingTwo TEXT, homework TEXT);")
            db?.execSQL("CREATE TABLE TEACHERS(_id INTEGER PRIMARY KEY AUTOINCREMENT, name INTEGER);")
        }
        if(newVersion >= 2){
            db?.execSQL("CREATE TABLE TIMETABLE (_id INTEGER PRIMARY KEY, name TEXT, lessonsOrder TEXT, dayOfWeek TEXT);")
        }
        if(newVersion >= 4){
            db?.execSQL("ALTER TABLE LESSONS_CARDS ADD lessonsOrder TEXT")
        }
        if(newVersion >= 5){
            db?.execSQL("CREATE TABLE NOTES (_id INTEGER PRIMARY KEY AUTOINCREMENT, headline TEXT, mainText TEXT, time TEXT, date TEXT, color TEXT)")
            db?.execSQL("DROP TABLE TIMETABLE")
        }
    }

}