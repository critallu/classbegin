package com.example.classroomassistant.ui.navigation

sealed class NavRoute(val route: String) {
    data object Home : NavRoute("home")
    data object Schedule : NavRoute("schedule")
    data object Countdown : NavRoute("countdown")
    data object Calendar : NavRoute("calendar")
    data object Settings : NavRoute("settings")
    data object CourseEdit : NavRoute("course_edit")
    data object CourseDetail : NavRoute("course_detail/{id}")
    data object EventEdit : NavRoute("event_edit")
}
