package com.example.classroomassistant.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.CalendarEvent
import com.example.classroomassistant.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {
    val events = repository.events.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val selectedDate = MutableStateFlow(java.time.LocalDate.now().toString())
    val snackbar = MutableStateFlow<String?>(null)

    fun save(event: CalendarEvent, onDone: () -> Unit) {
        if (event.title.isBlank()) { snackbar.value = "标题不能为空"; return }
        viewModelScope.launch {
            if (event.id == 0L) repository.add(event) else repository.update(event)
            snackbar.value = "事项已保存"
            onDone()
        }
    }

    fun delete(event: CalendarEvent) = viewModelScope.launch { repository.delete(event); snackbar.value = "事项已删除" }
    fun clearSnackbar() { snackbar.value = null }
}

class CalendarVmFactory(private val repository: CalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CalendarViewModel(repository) as T
}
