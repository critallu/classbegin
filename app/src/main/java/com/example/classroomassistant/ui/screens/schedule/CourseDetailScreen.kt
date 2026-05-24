package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.ReminderRule
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun CourseDetailScreen(course: ScheduledCourse?, vm: ScheduleViewModel, onStart: () -> Unit, onBack: () -> Unit) {
    var showDeleteCourseConfirm by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderRule?>(null) }
    var showScopeDialog by remember { mutableStateOf(false) }

    var editName by remember(course?.baseCourseId) { mutableStateOf(course?.name ?: "") }
    var editWeekday by remember(course?.baseCourseId) { mutableStateOf((course?.weekday ?: 1).toString()) }
    var editStartTime by remember(course?.baseCourseId) { mutableStateOf(course?.startTime ?: "") }
    var editDuration by remember(course?.baseCourseId) { mutableStateOf((course?.durationMinutes ?: 40).toString()) }
    var editClassroom by remember(course?.baseCourseId) { mutableStateOf(course?.classroom ?: "") }
    var editClassName by remember(course?.baseCourseId) { mutableStateOf(course?.className ?: "") }

    val reminderLabel = remember { mutableStateOf("") }
    val reminderMinutes = remember { mutableStateOf("5") }

    if (course == null) return
    val reminders by vm.observeReminders(course.baseCourseId).collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("编辑本课")
        OutlinedTextField(editName, { editName = it }, label = { Text("课程名") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(editClassName, { editClassName = it }, label = { Text("班级") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(editWeekday, { editWeekday = it }, label = { Text("星期") }, modifier = Modifier.weight(1f))
            OutlinedTextField(editStartTime, { editStartTime = it }, label = { Text("第几节/时间") }, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(editDuration, { editDuration = it }, label = { Text("时长") }, modifier = Modifier.weight(1f))
            OutlinedTextField(editClassroom, { editClassroom = it }, label = { Text("教室") }, modifier = Modifier.weight(1f))
        }

        PrimaryButton("开始上课", onStart)
        PrimaryButton("保存课程修改") { showScopeDialog = true }

        Text("倒计时提醒")
        OutlinedTextField(reminderLabel.value, { reminderLabel.value = it }, label = { Text("提醒内容") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(reminderMinutes.value, { reminderMinutes.value = it }, label = { Text("开课后几分钟") }, modifier = Modifier.fillMaxWidth())
        PrimaryButton("添加提醒") {
            vm.addReminder(course.baseCourseId, reminderLabel.value, reminderMinutes.value.toIntOrNull() ?: -1)
            reminderLabel.value = ""
        }

        reminders.forEach { rule ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${rule.triggerAfterMinutes}分钟后：${rule.label}")
                Row {
                    TextButton(onClick = { editingReminder = rule }) { Text("编辑") }
                    TextButton(onClick = { vm.deleteReminder(rule.id) }) { Text("删除") }
                }
            }
        }

        PrimaryButton("删除课程") { showDeleteCourseConfirm = true }
    }

    if (showScopeDialog) {
        AlertDialog(
            onDismissRequest = { showScopeDialog = false },
            title = { Text("应用范围") },
            text = { Text("这次修改只应用本周，还是应用到所有周？") },
            confirmButton = {
                TextButton(onClick = {
                    vm.updateScheduledCourse(
                        edited = course.copy(
                            name = editName,
                            weekday = editWeekday.toIntOrNull() ?: course.weekday,
                            startTime = editStartTime,
                            durationMinutes = editDuration.toIntOrNull() ?: course.durationMinutes,
                            classroom = editClassroom,
                            className = editClassName
                        ),
                        scope = UpdateScope.ALL_WEEKS,
                        onDone = { showScopeDialog = false }
                    )
                }) { Text("应用到所有周") }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.updateScheduledCourse(
                        edited = course.copy(
                            name = editName,
                            weekday = editWeekday.toIntOrNull() ?: course.weekday,
                            startTime = editStartTime,
                            durationMinutes = editDuration.toIntOrNull() ?: course.durationMinutes,
                            classroom = editClassroom,
                            className = editClassName
                        ),
                        scope = UpdateScope.CURRENT_WEEK,
                        onDone = { showScopeDialog = false }
                    )
                }) { Text("只改本周") }
            }
        )
    }

    if (editingReminder != null) {
        var editLabel by remember(editingReminder?.id) { mutableStateOf(editingReminder?.label ?: "") }
        var editMinutes by remember(editingReminder?.id) { mutableStateOf((editingReminder?.triggerAfterMinutes ?: 5).toString()) }
        AlertDialog(
            onDismissRequest = { editingReminder = null },
            confirmButton = {
                TextButton(onClick = {
                    val target = editingReminder ?: return@TextButton
                    vm.updateReminder(target, editLabel, editMinutes.toIntOrNull() ?: -1)
                    editingReminder = null
                }) { Text("保存") }
            },
            dismissButton = { TextButton(onClick = { editingReminder = null }) { Text("取消") } },
            title = { Text("编辑提醒") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(editLabel, { editLabel = it }, label = { Text("提醒内容") })
                    OutlinedTextField(editMinutes, { editMinutes = it }, label = { Text("开课后几分钟") })
                }
            }
        )
    }

    if (showDeleteCourseConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteCourseConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCourse(course.baseCourseId)
                    showDeleteCourseConfirm = false
                    onBack()
                }) { Text("确认删除") }
            },
            dismissButton = { TextButton(onClick = { showDeleteCourseConfirm = false }) { Text("取消") } },
            title = { Text("删除课程") },
            text = { Text("删除后将同步删除提醒规则，是否继续？") }
        )
    }
}
