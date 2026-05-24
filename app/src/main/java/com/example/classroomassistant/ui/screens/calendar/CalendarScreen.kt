package com.example.classroomassistant.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.ui.components.EmptyState

@Composable
fun CalendarScreen(vm: CalendarViewModel, onAdd: () -> Unit) {
    val events by vm.events.collectAsState()
    val selectedDate by vm.selectedDate.collectAsState()
    val daily = events.filter { it.date == selectedDate }
    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("日历", style = MaterialTheme.typography.titleLarge)
            Text(selectedDate, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)).padding(12.dp).clickable {
                vm.selectedDate.value = java.time.LocalDate.now().toString()
            })
            if (daily.isEmpty()) EmptyState("当天暂无事项") else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(daily) { e ->
                    Text("${if (e.important) "[重要] " else ""}${e.time} ${e.title}", modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)).padding(12.dp))
                }
            }
        }
    }
}
