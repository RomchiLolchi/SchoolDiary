package com.easyeducation.schooldiary

/**Класс для удобного заполнения данными recycler view в layout'е старого пользователя*/
data class Lesson(var id: Int, var name: String, var color: String, var date: String, var dayOfWeek: String, var time: String, var teachers: String, var ratingOne: String, var ratingTwo: String, var homework: String, var order: String) {

    init {
        lessonInit(id, name, color, date, dayOfWeek, time, teachers, ratingOne, ratingTwo, homework, order)
    }

    companion object{

        /**Переменная id урока в базе данных*/
        var lessonsId = 0
        /**Переменная названия урока*/
        lateinit var lessonsName: String
        /**Переменная цвета карточки урока*/
        lateinit var lessonsColor: String
        /**Переменная даты урока*/
        lateinit var lessonsDate: String
        /**Переменная, хранящая значение дня недели урока*/
        lateinit var lessonsDayOfWeek: String
        /**Переменная времени урока*/
        lateinit var lessonsTime: String
        /**Переменная учителя урока*/
        lateinit var lessonsTeacher: String
        /**Переменная первой оценки за урок*/
        lateinit var firstLessonsRating: String
        /**Переменная второй оценки за урок*/
        lateinit var secondLessonsRating: String
        /**Переменная домашнего задания*/
        lateinit var lessonsHomework: String
        /**Переменная порядка урока*/
        lateinit var lessonsOrder: String

        /**Метод инициализации (главный метод в классе)*/
        fun lessonInit(id: Int, name: String, color: String, date: String, dayOfWeek: String, time: String, teachers: String, ratingOne: String, ratingTwo: String, homework: String, order: String){
            lessonsName = name
            lessonsColor = color
            lessonsDate = date
            lessonsDayOfWeek = dayOfWeek
            lessonsTime = time
            lessonsTeacher = teachers
            firstLessonsRating = ratingOne
            secondLessonsRating = ratingTwo
            lessonsHomework = homework
            lessonsOrder = order
            lessonsId = id
        }
    }

}