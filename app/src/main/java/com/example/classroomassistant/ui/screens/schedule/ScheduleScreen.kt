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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.ui.components.CourseCard
import com.example.classroomassistant.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(vm: ScheduleViewModel, onAdd: () -> Unit, onDetail: (Long) -> Unit) {
    val courses by vm.courses.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "新增") }
        }
    ) { padding ->
        if (courses.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) { EmptyState("还没有课程，点击右下角新增。") }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(courses) { c ->
                    CourseCard(c, highlight = c.weekday == java.time.LocalDate.now().dayOfWeek.value) { onDetail(c.id) }
                }
            }
        }
    }
}
