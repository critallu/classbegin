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
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.entity.ReminderRule
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun CourseDetailScreen(
    course: Course?,
    vm: ScheduleViewModel,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onEditCourse: (Long) -> Unit
) {
    var showDeleteCourseConfirm by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderRule?>(null) }
    val reminderLabel = remember { mutableStateOf("") }
    val reminderMinutes = remember { mutableStateOf("5") }

    if (course == null) return
    val reminders by vm.observeReminders(course.id).collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(course.name)
        Text("班级: ${course.className}")
        Text("时间: 周${course.weekday} ${course.startTime}")
        Text("地点: ${course.classroom}")
        Text("时长: ${course.durationMinutes}分钟")
        Text("备注: ${course.note}")
        PrimaryButton("开始上课", onStart)
        PrimaryButton("编辑课程") { onEditCourse(course.id) }

        Text("倒计时提醒")
        OutlinedTextField(
            value = reminderLabel.value,
            onValueChange = { reminderLabel.value = it },
            label = { Text("提醒内容") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = reminderMinutes.value,
            onValueChange = { reminderMinutes.value = it },
            label = { Text("开课后几分钟") },
            modifier = Modifier.fillMaxWidth()
        )
        PrimaryButton("添加提醒") {
            vm.addReminder(course.id, reminderLabel.value, reminderMinutes.value.toIntOrNull() ?: -1)
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
                    vm.deleteCourse(course)
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
