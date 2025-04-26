package com.example.coctailapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.isActive


class TimerViewModel : ViewModel() {
    private val _timeLeft = mutableStateOf(0)
    val timeLeft: State<Int> = _timeLeft

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    private var job: Job? = null

    fun startTimer(seconds: Int = 60) {
        if (_isRunning.value) return

        job?.cancel()
        job = viewModelScope.launch {
            _isRunning.value = true
            _timeLeft.value = seconds

            while (_timeLeft.value > 0 && isActive) {
                delay(1000)
                _timeLeft.value -= 1
            }

            _isRunning.value = false
        }
    }

    fun stopTimer() {
        job?.cancel()
        _isRunning.value = false
    }

    fun resetTimer() {
        stopTimer()
        _timeLeft.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
