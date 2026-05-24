package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.ui.components.CourseCard
import com.example.classroomassistant.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(vm: ScheduleViewModel, onAdd: () -> Unit, onDetail: (Long) -> Unit) {
    val courses by vm.scheduledCourses.collectAsState()
    val semester by vm.semester.collectAsState()
    val week by vm.currentWeek().collectAsState()

    val totalWeeksInput = remember(semester.totalWeeks) { mutableStateOf(semester.totalWeeks.toString()) }
    val currentWeekInput = remember(week) { mutableStateOf(week.toString()) }
    val startDateInput = remember(semester.termStartDate) { mutableStateOf(semester.termStartDate) }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "新增") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("学期设置")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(totalWeeksInput.value, { totalWeeksInput.value = it }, label = { Text("总周数") }, modifier = Modifier.weight(1f))
                OutlinedTextField(currentWeekInput.value, { currentWeekInput.value = it }, label = { Text("当前周") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(startDateInput.value, { startDateInput.value = it }, label = { Text("开学日期") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                vm.saveSemester(
                    totalWeeks = totalWeeksInput.value.toIntOrNull() ?: semester.totalWeeks,
                    currentWeek = currentWeekInput.value.toIntOrNull() ?: week,
                    termStartDate = startDateInput.value
                )
            }) { Text("保存学期") }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.setWeek(week - 1) }) { Text("上一周") }
                Text("第 $week / ${semester.totalWeeks} 周", modifier = Modifier.padding(top = 10.dp))
                Button(onClick = { vm.setWeek(week + 1) }) { Text("下一周") }
            }

            if (courses.isEmpty()) {
                EmptyState("本周没有课程")
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(courses) { sc ->
                        val c = Course(
                            id = sc.baseCourseId,
                            name = sc.name,
                            weekday = sc.weekday,
                            startTime = sc.startTime,
                            durationMinutes = sc.durationMinutes,
                            classroom = sc.classroom,
                            className = sc.className,
                            note = sc.note,
                            color = sc.color
                        )
                        CourseCard(c, highlight = sc.weekday == java.time.LocalDate.now().dayOfWeek.value) { onDetail(sc.baseCourseId) }
                    }
                }
            }
        }
    }
}
