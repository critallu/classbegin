package com.example.classroomassistant.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.entity.CourseWeekOverride
import com.example.classroomassistant.data.entity.ReminderRule
import com.example.classroomassistant.data.entity.SemesterConfig
import com.example.classroomassistant.data.repository.CourseRepository
import com.example.classroomassistant.data.repository.CourseWeekOverrideRepository
import com.example.classroomassistant.data.repository.ReminderRepository
import com.example.classroomassistant.data.repository.SemesterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UpdateScope { CURRENT_WEEK, ALL_WEEKS }

data class ScheduledCourse(
    val baseCourseId: Long,
    val sourceOverrideId: Long?,
    val name: String,
    val weekday: Int,
    val startTime: String,
    val durationMinutes: Int,
    val classroom: String,
    val className: String,
    val note: String,
    val color: String
)

class ScheduleViewModel(
    private val courseRepository: CourseRepository,
    private val reminderRepository: ReminderRepository,
    private val semesterRepository: SemesterRepository,
    private val overrideRepository: CourseWeekOverrideRepository
) : ViewModel() {
    val snackbar = MutableStateFlow<String?>(null)
    private val selectedWeek = MutableStateFlow(1)

    val semester: StateFlow<SemesterConfig> = semesterRepository.config
        .map { it ?: SemesterConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SemesterConfig())

    private val templates = courseRepository.courses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val weekOverrides = selectedWeek
        .flatMapLatest { overrideRepository.observeByWeek(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduledCourses: StateFlow<List<ScheduledCourse>> = combine(templates, weekOverrides) { base, overrides ->
        val overrideByBase = overrides.associateBy { it.baseCourseId }
        base.map { c ->
            val o = overrideByBase[c.id]
            if (o != null) {
                ScheduledCourse(c.id, o.id, o.name, o.weekday, o.startTime, o.durationMinutes, o.classroom, o.className, o.note, o.color)
            } else {
                ScheduledCourse(c.id, null, c.name, c.weekday, c.startTime, c.durationMinutes, c.classroom, c.className, c.note, c.color)
            }
        }.sortedWith(compareBy<ScheduledCourse> { it.weekday }.thenBy { it.startTime })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun currentWeek(): StateFlow<Int> = selectedWeek

    fun setWeek(week: Int) {
        val total = semester.value.totalWeeks.coerceAtLeast(1)
        selectedWeek.value = week.coerceIn(1, total)
    }

    fun saveSemester(totalWeeks: Int, currentWeek: Int, termStartDate: String) {
        viewModelScope.launch {
            val cfg = SemesterConfig(totalWeeks = totalWeeks.coerceAtLeast(1), currentWeek = currentWeek.coerceAtLeast(1), termStartDate = termStartDate)
            semesterRepository.upsert(cfg)
            selectedWeek.value = cfg.currentWeek.coerceIn(1, cfg.totalWeeks)
            snackbar.value = "学期设置已保存"
        }
    }

    fun observeReminders(courseId: Long): StateFlow<List<ReminderRule>> =
        reminderRepository.observeByCourse(courseId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(courseId: Long, label: String, afterMinutes: Int) {
        if (label.isBlank() || afterMinutes <= 0) { snackbar.value = "提醒内容和分钟数必须有效"; return }
        viewModelScope.launch {
            reminderRepository.add(ReminderRule(courseId = courseId, triggerAfterMinutes = afterMinutes, label = label, enabled = true))
            snackbar.value = "提醒已添加"
        }
    }

    fun updateReminder(reminder: ReminderRule, label: String, afterMinutes: Int) {
        if (label.isBlank() || afterMinutes <= 0) { snackbar.value = "提醒内容和分钟数必须有效"; return }
        viewModelScope.launch {
            reminderRepository.update(reminder.copy(label = label, triggerAfterMinutes = afterMinutes))
            snackbar.value = "提醒已更新"
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch { reminderRepository.deleteById(reminderId); snackbar.value = "提醒已删除" }
    }

    fun saveCourse(course: Course, onDone: () -> Unit) {
        if (course.name.isBlank() || course.startTime.isBlank() || course.weekday !in 1..7 || course.durationMinutes <= 0) {
            snackbar.value = "请完整填写必填项"; return
        }
        viewModelScope.launch {
            if (course.id == 0L) courseRepository.add(course) else courseRepository.update(course)
            snackbar.value = "保存成功"
            onDone()
        }
    }

    fun updateScheduledCourse(edited: ScheduledCourse, scope: UpdateScope, onDone: () -> Unit) {
        viewModelScope.launch {
            if (scope == UpdateScope.ALL_WEEKS) {
                courseRepository.update(
                    Course(
                        id = edited.baseCourseId,
                        name = edited.name,
                        weekday = edited.weekday,
                        startTime = edited.startTime,
                        durationMinutes = edited.durationMinutes,
                        classroom = edited.classroom,
                        className = edited.className,
                        note = edited.note,
                        color = edited.color
                    )
                )
            } else {
                val week = selectedWeek.value
                val existing = overrideRepository.findByBaseAndWeek(edited.baseCourseId, week)
                val override = CourseWeekOverride(
                    id = existing?.id ?: 0,
                    baseCourseId = edited.baseCourseId,
                    weekIndex = week,
                    name = edited.name,
                    weekday = edited.weekday,
                    startTime = edited.startTime,
                    durationMinutes = edited.durationMinutes,
                    classroom = edited.classroom,
                    className = edited.className,
                    note = edited.note,
                    color = edited.color
                )
                if (existing == null) overrideRepository.add(override) else overrideRepository.update(override)
            }
            snackbar.value = if (scope == UpdateScope.ALL_WEEKS) "已应用到所有周" else "仅本周已修改"
            onDone()
        }
    }

    fun deleteCourse(baseCourseId: Long) {
        val base = templates.value.firstOrNull { it.id == baseCourseId } ?: return
        viewModelScope.launch {
            courseRepository.delete(base)
            snackbar.value = "已删除课程"
        }
    }

    fun clearSnackbar() { snackbar.value = null }
}

class ScheduleVmFactory(
    private val courseRepository: CourseRepository,
    private val reminderRepository: ReminderRepository,
    private val semesterRepository: SemesterRepository,
    private val overrideRepository: CourseWeekOverrideRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ScheduleViewModel(courseRepository, reminderRepository, semesterRepository, overrideRepository) as T
}
