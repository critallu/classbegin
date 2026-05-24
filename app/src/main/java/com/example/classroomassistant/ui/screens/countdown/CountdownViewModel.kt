package com.example.classroomassistant.ui.screens.countdown

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classroomassistant.data.entity.Course
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CountdownUiState(
    val course: Course? = null,
    val totalSec: Int = 0,
    val remainSec: Int = 0,
    val running: Boolean = false,
    val tip: String = ""
)

class CountdownViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CountdownUiState())
    val uiState: StateFlow<CountdownUiState> = _uiState
    private var ticker: Job? = null
    private val reminded = mutableSetOf<Int>()

    fun start(course: Course) {
        val total = course.durationMinutes * 60
        _uiState.value = CountdownUiState(course, total, total, true)
        reminded.clear()
        runTicker()
    }

    private fun runTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (_uiState.value.running && _uiState.value.remainSec > 0) {
                delay(1000)
                val newSec = _uiState.value.remainSec - 1
                _uiState.value = _uiState.value.copy(remainSec = newSec)
                val m = newSec / 60
                listOf(20, 10, 5, 0).forEach {
                    if (m == it && reminded.add(it)) {
                        _uiState.value = _uiState.value.copy(tip = if (it == 0) "下课时间到" else "距离下课还有${it}分钟")
                    }
                }
            }
            if (_uiState.value.remainSec <= 0) _uiState.value = _uiState.value.copy(running = false)
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

class CountdownVmFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CountdownViewModel() as T
}
