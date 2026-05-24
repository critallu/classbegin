package com.example.classroomassistant.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.data.entity.CalendarEvent
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun EventEditScreen(initialDate: String, vm: CalendarViewModel, onDone: () -> Unit) {
    val title = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(initialDate) }
    val time = remember { mutableStateOf("17:00") }
    val content = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("教学") }
    val important = remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("新增事项")
        OutlinedTextField(title.value, { title.value = it }, label = { Text("标题*") })
        OutlinedTextField(date.value, { date.value = it }, label = { Text("日期") })
        OutlinedTextField(time.value, { time.value = it }, label = { Text("时间") })
        OutlinedTextField(type.value, { type.value = it }, label = { Text("类型") })
        OutlinedTextField(content.value, { content.value = it }, label = { Text("备注") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("重要事项")
            Switch(checked = important.value, onCheckedChange = { important.value = it })
        }
        PrimaryButton("保存") {
            vm.save(
                CalendarEvent(
                    title = title.value,
                    date = date.value,
                    time = time.value,
                    content = content.value,
                    type = type.value,
                    important = important.value
                ),
                onDone
            )
        }
    }
}
