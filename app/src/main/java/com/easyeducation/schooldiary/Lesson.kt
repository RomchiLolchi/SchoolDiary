package com.easyeducation.schooldiary

/**Класс для удобного заполнения данными recycler view в layout'е старого пользователя*/
data class Lesson(val name: String, val color: String, val date: String, val dayOfWeek: String, val time: String, val teachers: String, val ratingOne: String, val ratingTwo: String, val homework: String, val order: String) {

    init {
        lessonInit(name, color, date, dayOfWeek, time, teachers, ratingOne, ratingTwo, homework, order)
    }

    companion object{

        /**Переменная названия урока*/
        public lateinit var lessonsName: String
        /**Переменная цвета карточки урока*/
        public lateinit var lessonsColor: String
        /**Переменная даты урока*/
        public lateinit var lessonsDate: String
        /**Переменная, хранящая значение дня недели урока*/
        public lateinit var lessonsDayOfWeek: String
        /**Переменная времени урока*/
        public lateinit var lessonsTime: String
        /**Переменная учителя урока*/
        public lateinit var lessonsTeacher: String
        /**Переменная первой оценки за урок*/
        public lateinit var firstLessonsRating: String
        /**Переменная второй оценки за урок*/
        public lateinit var secondLessonsRating: String
        /**Переменная домашнего задания*/
        public lateinit var lessonsHomework: String
        /**Переменная порядка урока*/
        public lateinit var lessonsOrder: String

        /**Метод инициализации (главный метод в классе)*/
        fun lessonInit(name: String, color: String, date: String, dayOfWeek: String, time: String, teachers: String, ratingOne: String, ratingTwo: String, homework: String, order: String){
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
        }
    }

}