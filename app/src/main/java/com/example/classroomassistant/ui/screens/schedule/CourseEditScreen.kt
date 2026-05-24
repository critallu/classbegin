package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun CourseEditScreen(vm: ScheduleViewModel, onDone: () -> Unit) {
    val name = remember { mutableStateOf("") }
    val weekday = remember { mutableIntStateOf(java.time.LocalDate.now().dayOfWeek.value) }
    val startTime = remember { mutableStateOf("08:00") }
    val duration = remember { mutableIntStateOf(40) }
    val classroom = remember { mutableStateOf("") }
    val className = remember { mutableStateOf("") }
    val note = remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("新增课程")
        OutlinedTextField(name.value, { name.value = it }, label = { Text("课程名称* ") })
        OutlinedTextField(weekday.intValue.toString(), { weekday.intValue = it.toIntOrNull() ?: 1 }, label = { Text("星期(1-7)*") })
        OutlinedTextField(startTime.value, { startTime.value = it }, label = { Text("开始时间* 例如 08:00") })
        OutlinedTextField(duration.intValue.toString(), { duration.intValue = it.toIntOrNull() ?: 40 }, label = { Text("时长分钟* ") })
        OutlinedTextField(classroom.value, { classroom.value = it }, label = { Text("教室") })
        OutlinedTextField(className.value, { className.value = it }, label = { Text("班级") })
        OutlinedTextField(note.value, { note.value = it }, label = { Text("备注") })
        PrimaryButton("保存") {
            vm.saveCourse(
                Course(
                    name = name.value,
                    weekday = weekday.intValue,
                    startTime = startTime.value,
                    durationMinutes = duration.intValue,
                    classroom = classroom.value,
                    className = className.value,
                    note = note.value,
                    color = "#E8F0FA"
                ),
                onDone = onDone
            )
        }
    }
}
