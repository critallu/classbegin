package com.example.classroomassistant.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import com.example.classroomassistant.data.entity.CalendarEvent
import com.example.classroomassistant.ui.components.EmptyState
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarScreen(vm: CalendarViewModel, onAdd: (String) -> Unit) {
    val events by vm.events.collectAsState()
    val selectedDate by vm.selectedDate.collectAsState()
    var viewMode by remember { mutableIntStateOf(1) } // 0年 1月 2周 3日

    val selected = LocalDate.parse(selectedDate)
    val visibleEvents = filterEventsByMode(events, selected, viewMode)

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { onAdd(selectedDate) }) { Text("+") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("日历", style = MaterialTheme.typography.titleLarge)
            TabRow(selectedTabIndex = viewMode) {
                Tab(selected = viewMode == 0, onClick = { viewMode = 0 }, text = { Text("年") })
                Tab(selected = viewMode == 1, onClick = { viewMode = 1 }, text = { Text("月") })
                Tab(selected = viewMode == 2, onClick = { viewMode = 2 }, text = { Text("周") })
                Tab(selected = viewMode == 3, onClick = { viewMode = 3 }, text = { Text("日") })
            }

            DateSelector(selected = selectedDate, onDateClick = { vm.selectedDate.value = it })

            if (visibleEvents.isEmpty()) {
                EmptyState("当前视图没有事项")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(visibleEvents) { e -> EventRow(e) }
                }
            }
        }
    }
}

@Composable
private fun DateSelector(selected: String, onDateClick: (String) -> Unit) {
    val base = LocalDate.parse(selected)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        (-3..3).forEach { offset ->
            val d = base.plusDays(offset.toLong())
            Text(
                text = d.dayOfMonth.toString(),
                modifier = Modifier
                    .weight(1f)
                    .background(if (d.toString() == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                    .padding(8.dp)
                    .clickable { onDateClick(d.toString()) }
            )
        }
    }
}

@Composable
private fun EventRow(e: CalendarEvent) {
    Text(
        "${e.date} ${e.time} ${if (e.important) "[重要] " else ""}${e.title}",
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)).padding(12.dp)
    )
}

private fun filterEventsByMode(events: List<CalendarEvent>, selected: LocalDate, mode: Int): List<CalendarEvent> {
    return events.filter {
        val d = LocalDate.parse(it.date)
        when (mode) {
            0 -> d.year == selected.year
            1 -> d.year == selected.year && d.month == selected.month
            2 -> {
                val wf = WeekFields.of(Locale.getDefault())
                d.get(wf.weekOfWeekBasedYear()) == selected.get(wf.weekOfWeekBasedYear()) && d.year == selected.year
            }
            else -> d == selected
        }
    }.sortedBy { "${it.date} ${it.time}" }
}
