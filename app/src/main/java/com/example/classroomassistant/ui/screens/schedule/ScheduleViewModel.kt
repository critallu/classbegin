package com.example.classroomassistant.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: CourseRepository) : ViewModel() {
    val courses = repository.courses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val snackbar = MutableStateFlow<String?>(null)

    fun saveCourse(course: Course, onDone: () -> Unit) {
        if (course.name.isBlank() || course.startTime.isBlank() || course.weekday !in 1..7 || course.durationMinutes <= 0) {
            snackbar.value = "请完整填写必填项"
            return
        }
        viewModelScope.launch {
            if (course.id == 0L) repository.add(course) else repository.update(course)
            snackbar.value = "保存成功"
            onDone()
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.delete(course)
            snackbar.value = "已删除课程"
        }
    }

    fun clearSnackbar() { snackbar.value = null }
}

class ScheduleVmFactory(private val repository: CourseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ScheduleViewModel(repository) as T
}
