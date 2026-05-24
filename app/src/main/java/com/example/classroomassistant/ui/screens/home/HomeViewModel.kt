package com.example.classroomassistant.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.AppSettings
import com.example.classroomassistant.data.entity.CalendarEvent
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.repository.CalendarRepository
import com.example.classroomassistant.data.repository.CourseRepository
import com.example.classroomassistant.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class HomeUiState(
    val greeting: String = "上午好",
    val teacherName: String = "Ada",
    val todayCourses: List<Course> = emptyList(),
    val todayEvents: List<CalendarEvent> = emptyList(),
    val nextCourse: Course? = null
)

class HomeViewModel(
    courseRepository: CourseRepository,
    calendarRepository: CalendarRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        courseRepository.courses,
        calendarRepository.events,
        settingsRepository.settings
    ) { courses, events, settings ->
        val today = LocalDate.now()
        val weekday = today.dayOfWeek.value
        val dateStr = today.toString()
        val todayCourses = courses.filter { it.weekday == weekday }
        val importantEvents = events.filter { it.date == dateStr && it.important }
        HomeUiState(
            greeting = when (java.time.LocalTime.now().hour) {
                in 0..11 -> "上午好"
                in 12..17 -> "下午好"
                else -> "晚上好"
            },
            teacherName = (settings ?: AppSettings()).teacherName,
            todayCourses = todayCourses,
            todayEvents = importantEvents,
            nextCourse = todayCourses.firstOrNull()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}

class HomeVmFactory(
    private val c: CourseRepository,
    private val e: CalendarRepository,
    private val s: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(c, e, s) as T
}
