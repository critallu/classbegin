package com.example.classroomassistant.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.ui.components.CourseCard
import com.example.classroomassistant.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(vm: ScheduleViewModel, onAdd: () -> Unit, onDetail: (Long) -> Unit) {
    val courses by vm.courses.collectAsState()
    var mode by remember { mutableIntStateOf(0) } // 0 week, 1 day
    val todayWeekday = java.time.LocalDate.now().dayOfWeek.value

    val shownCourses = if (mode == 0) {
        courses
    } else {
        courses.filter { it.weekday == todayWeekday }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "新增") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = mode) {
                Tab(selected = mode == 0, onClick = { mode = 0 }, text = { Text("周视图") })
                Tab(selected = mode == 1, onClick = { mode = 1 }, text = { Text("日视图") })
            }

            if (shownCourses.isEmpty()) {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    EmptyState(if (mode == 0) "本周没有课程" else "今天没有课程")
                }
            } else {
                if (mode == 0) {
                    WeeklyCourseList(courses = shownCourses, onDetail = onDetail)
                } else {
                    DailyCourseList(courses = shownCourses, onDetail = onDetail)
                }
            }
        }
    }
}

@Composable
private fun WeeklyCourseList(courses: List<Course>, onDetail: (Long) -> Unit) {
    val grouped = courses.groupBy { it.weekday }.toSortedMap()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        grouped.forEach { (weekday, dayCourses) ->
            item { Text("周${toCnWeekday(weekday)}") }
            items(dayCourses) { c ->
                CourseCard(c, highlight = c.weekday == java.time.LocalDate.now().dayOfWeek.value) { onDetail(c.id) }
            }
        }
    }
}

@Composable
private fun DailyCourseList(courses: List<Course>, onDetail: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(courses) { c -> CourseCard(c, highlight = true) { onDetail(c.id) } }
    }
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
