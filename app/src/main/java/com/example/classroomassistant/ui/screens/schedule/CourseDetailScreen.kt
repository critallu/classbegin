package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun CourseDetailScreen(course: Course?, vm: ScheduleViewModel, onStart: () -> Unit, onBack: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    if (course == null) return
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(course.name)
        Text("班级: ${course.className}")
        Text("时间: 周${course.weekday} ${course.startTime}")
        Text("地点: ${course.classroom}")
        Text("时长: ${course.durationMinutes}分钟")
        Text("备注: ${course.note}")
        PrimaryButton("开始上课", onStart)
        PrimaryButton("删除课程") { showConfirm = true }
    }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = { TextButton(onClick = { vm.deleteCourse(course); showConfirm = false; onBack() }) { Text("确认删除") } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("取消") } },
            title = { Text("删除课程") },
            text = { Text("删除后将同步删除提醒规则，是否继续？") }
        )
    }
}
