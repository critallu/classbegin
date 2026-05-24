package com.example.classroomassistant.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.entity.ReminderRule
import com.example.classroomassistant.data.repository.CourseRepository
import com.example.classroomassistant.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: CourseRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    val courses = repository.courses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val snackbar = MutableStateFlow<String?>(null)

    fun observeReminders(courseId: Long): StateFlow<List<ReminderRule>> =
        reminderRepository.observeByCourse(courseId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(courseId: Long, label: String, afterMinutes: Int) {
        if (label.isBlank() || afterMinutes <= 0) {
            snackbar.value = "提醒内容和分钟数必须有效"
            return
        }
        viewModelScope.launch {
            reminderRepository.add(
                ReminderRule(courseId = courseId, triggerAfterMinutes = afterMinutes, label = label, enabled = true)
            )
            snackbar.value = "提醒已添加"
        }
    }

    fun updateReminder(reminder: ReminderRule, label: String, afterMinutes: Int) {
        if (label.isBlank() || afterMinutes <= 0) {
            snackbar.value = "提醒内容和分钟数必须有效"
            return
        }
        viewModelScope.launch {
            reminderRepository.update(reminder.copy(label = label, triggerAfterMinutes = afterMinutes))
            snackbar.value = "提醒已更新"
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch {
            reminderRepository.deleteById(reminderId)
            snackbar.value = "提醒已删除"
        }
    }

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

class ScheduleVmFactory(
    private val repository: CourseRepository,
    private val reminderRepository: ReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ScheduleViewModel(repository, reminderRepository) as T
}
