package com.easyeducation.schooldiary

data class Note(var headlineIn: String, var mainTextIn: String, var dateIn: String, var timeIn: String, var color: String) {
    init{
        dataInit(headlineIn, mainTextIn, dateIn, timeIn, color)
    }
    companion object {
        lateinit var headline: String
        lateinit var mainText: String
        lateinit var dateNote: String
        lateinit var timeNote: String
        lateinit var colorNote: String

        private fun dataInit(noteHeadline: String, noteMainText: String, noteDate: String, noteTime: String, noteColor: String){
            headline = noteHeadline
            mainText = noteMainText
            dateNote = noteDate
            timeNote = noteTime
            colorNote = noteColor
        }
    }
}