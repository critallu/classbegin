package com.example.classroomassistant.ui.screens.countdown

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.entity.ReminderRule
import com.example.classroomassistant.data.repository.ReminderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CountdownUiState(
    val course: Course? = null,
    val totalSec: Int = 0,
    val remainSec: Int = 0,
    val running: Boolean = false,
    val tip: String = "",
    val rules: List<ReminderRule> = emptyList(),
    val vibrateSignal: Int = 0
)

class CountdownViewModel(private val reminderRepository: ReminderRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CountdownUiState())
    val uiState: StateFlow<CountdownUiState> = _uiState
    private var ticker: Job? = null
    private val remindedIds = mutableSetOf<Long>()

    fun start(course: Course) {
        viewModelScope.launch {
            val rules = reminderRepository.observeByCourse(course.id).first().filter { it.enabled }
            val total = course.durationMinutes * 60
            _uiState.value = CountdownUiState(course = course, totalSec = total, remainSec = total, running = true, rules = rules)
            remindedIds.clear()
            runTicker()
        }
    }

    private fun runTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (_uiState.value.running && _uiState.value.remainSec > 0) {
                delay(1000)
                val current = _uiState.value
                val newSec = current.remainSec - 1
                val elapsedSec = current.totalSec - newSec
                var next = current.copy(remainSec = newSec)

                current.rules.forEach { rule ->
                    if (!remindedIds.contains(rule.id) && elapsedSec >= rule.triggerAfterMinutes * 60) {
                        remindedIds.add(rule.id)
                        next = next.copy(
                            tip = rule.label,
                            vibrateSignal = current.vibrateSignal + 1
                        )
                    }
                }

                if (newSec <= 0) {
                    next = next.copy(running = false, tip = "下课时间到", vibrateSignal = next.vibrateSignal + 1)
                }
                _uiState.value = next
            }
        }
    }

    fun pause() { _uiState.value = _uiState.value.copy(running = false); ticker?.cancel() }
    fun resume() { _uiState.value = _uiState.value.copy(running = true); runTicker() }
    fun end() { ticker?.cancel(); _uiState.value = _uiState.value.copy(running = false, remainSec = 0, tip = "课程已结束") }

    fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

class CountdownVmFactory(private val reminderRepository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CountdownViewModel(reminderRepository) as T
}
