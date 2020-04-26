package com.easyeducation.schooldiary

data class Note(
    var headlineIn: String,
    var mainTextIn: String,
    var dateIn: String,
    var timeIn: String,
    var color: String
) {
    init {
        dataInit(headlineIn, mainTextIn, dateIn, timeIn, color)
    }

    companion object {
        /**Заголовок заметки*/
        lateinit var headline: String

        /**Основной текст заметки*/
        lateinit var mainText: String

        /**Дата напоминания заметки*/
        lateinit var dateNote: String

        /**Время напоминания заметки*/
        lateinit var timeNote: String

        /**Цвет заметки*/
        lateinit var colorNote: String

        private fun dataInit(
            noteHeadline: String,
            noteMainText: String,
            noteDate: String,
            noteTime: String,
            noteColor: String
        ) {
            headline = noteHeadline
            mainText = noteMainText
            dateNote = noteDate
            timeNote = noteTime
            colorNote = noteColor
        }
    }
}