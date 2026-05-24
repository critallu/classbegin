package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val periodsInput = remember(semester.periodsPerDay) { mutableStateOf(semester.periodsPerDay.toString()) }
    val periodDurationInput = remember(semester.periodDurationMinutes) { mutableStateOf(semester.periodDurationMinutes.toString()) }

    val weekdays = listOf(1, 2, 3, 4, 5)
    val periods = (1..semester.periodsPerDay).toList()

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "新增") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("学期设置")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(totalWeeksInput.value, { totalWeeksInput.value = it }, label = { Text("总周数") }, modifier = Modifier.weight(1f))
                OutlinedTextField(currentWeekInput.value, { currentWeekInput.value = it }, label = { Text("当前周") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(periodsInput.value, { periodsInput.value = it }, label = { Text("每天几节") }, modifier = Modifier.weight(1f))
                OutlinedTextField(periodDurationInput.value, { periodDurationInput.value = it }, label = { Text("每节分钟") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(startDateInput.value, { startDateInput.value = it }, label = { Text("开学日期") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                vm.saveSemester(
                    totalWeeksInput.value.toIntOrNull() ?: semester.totalWeeks,
                    currentWeekInput.value.toIntOrNull() ?: week,
                    startDateInput.value,
                    periodsInput.value.toIntOrNull() ?: semester.periodsPerDay,
                    periodDurationInput.value.toIntOrNull() ?: semester.periodDurationMinutes
                )
            }) { Text("保存学期") }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.setWeek(week - 1) }) { Text("上一周") }
                Text("第 $week / ${semester.totalWeeks} 周 · 每节${semester.periodDurationMinutes}分钟", modifier = Modifier.padding(top = 10.dp))
                Button(onClick = { vm.setWeek(week + 1) }) { Text("下一周") }
            }

            if (courses.isEmpty()) {
                EmptyState("本周没有课程")
            } else {
                val scroll = rememberScrollState()
                Column(Modifier.horizontalScroll(scroll)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("节次", modifier = Modifier.padding(6.dp))
                        weekdays.forEach { wd ->
                            Text("周${toCnWeekday(wd)}", modifier = Modifier.padding(6.dp))
                        }
                    }
                    periods.forEach { p ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Cell(text = "第${p}节", width = 64.dp)
                            weekdays.forEach { wd ->
                                val hit = courses.firstOrNull { it.weekday == wd && parsePeriod(it.startTime) == p }
                                Cell(
                                    text = hit?.let { "${it.className}\n${it.name}" } ?: "",
                                    width = 94.dp,
                                    onClick = { if (hit != null) onDetail(hit.baseCourseId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Cell(text: String, width: Dp, onClick: (() -> Unit)? = null) {
    Text(
        text = text,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .background(Color(0xFFF7F9FB), RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(8.dp)
            .width(width),
        maxLines = 3
    )
}

private fun parsePeriod(startTime: String): Int {
    val n = startTime.trim().removePrefix("第").removeSuffix("节").toIntOrNull()
    return n ?: 1
}

private fun toCnWeekday(weekday: Int): String = when (weekday) {
    1 -> "一"
    2 -> "二"
    3 -> "三"
    4 -> "四"
    5 -> "五"
    6 -> "六"
    else -> "日"
}
