package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun CourseEditScreen(existing: Course?, vm: ScheduleViewModel, onDone: () -> Unit) {
    var name by remember(existing?.id) { mutableStateOf(existing?.name ?: "") }
    var weekday by remember(existing?.id) { mutableIntStateOf(existing?.weekday ?: java.time.LocalDate.now().dayOfWeek.value) }
    var startTime by remember(existing?.id) { mutableStateOf(existing?.startTime ?: "08:00") }
    var duration by remember(existing?.id) { mutableIntStateOf(existing?.durationMinutes ?: 40) }
    var classroom by remember(existing?.id) { mutableStateOf(existing?.classroom ?: "") }
    var className by remember(existing?.id) { mutableStateOf(existing?.className ?: "") }
    var note by remember(existing?.id) { mutableStateOf(existing?.note ?: "") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (existing == null) "新增课程" else "编辑课程")
        OutlinedTextField(name, { name = it }, label = { Text("课程名称* ") })
        OutlinedTextField(weekday.toString(), { weekday = it.toIntOrNull() ?: 1 }, label = { Text("星期(1-7)*") })
        OutlinedTextField(startTime, { startTime = it }, label = { Text("开始时间* 例如 08:00") })
        OutlinedTextField(duration.toString(), { duration = it.toIntOrNull() ?: 40 }, label = { Text("时长分钟* ") })
        OutlinedTextField(classroom, { classroom = it }, label = { Text("教室") })
        OutlinedTextField(className, { className = it }, label = { Text("班级") })
        OutlinedTextField(note, { note = it }, label = { Text("备注") })
        PrimaryButton("保存") {
            vm.saveCourse(
                Course(
                    id = existing?.id ?: 0,
                    name = name,
                    weekday = weekday,
                    startTime = startTime,
                    durationMinutes = duration,
                    classroom = classroom,
                    className = className,
                    note = note,
                    color = existing?.color ?: "#E8F0FA"
                ),
                onDone = onDone
            )
        }
    }
}
