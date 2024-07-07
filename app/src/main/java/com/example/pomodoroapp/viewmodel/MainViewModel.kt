package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.R
import com.example.pomodoroapp.data.LocalStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) :  AndroidViewModel(application) {
    private val localStorage = LocalStorage(application)

    enum class Mode(val modeName: String, var duration: Int, val backgroundColor: Int, val buttonColor: Int, val clockColor: Int, val playColor: Int) {
        POMO("Focus", 25, R.color.background_color_red_50, R.color.focus_color_red_alpha_100, R.color.clock_color_red_900, R.color.play_color_red_alpha_700),
        SHORT_BREAK("Short Break", 5, R.color.background_color_green_50, R.color.short_color_green_alpha_100, R.color.clock_color_green_900, R.color.play_color_green_alpha_600),
        LONG_BREAK("Long Break", 15, R.color.background_color_blue_50, R.color.long_color_blue_alpha_100, R.color.clock_color_blue_900, R.color.play_color_blue_alpha_600)
    }



    private var _modeCounter = 0
    private val _mode = MutableLiveData<Mode>(Mode.POMO)
    private val _isPlaying = MutableLiveData<Boolean>(false)
    private val _time = MutableLiveData<Int>(_mode.value!!.duration * 60)
    private var timerJob: Job? = null

    private val _soundEnabled = MutableLiveData<Boolean>()

    init {
        _soundEnabled.value = localStorage.getSoundInfo()
        println(soundEnabled.value)
    }


    val mode: LiveData<Mode> get() = _mode
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val time: LiveData<Int> get() = _time
    val soundEnabled: LiveData<Boolean> get() = _soundEnabled


    fun togglePlayPause() {
        _isPlaying.value = !(_isPlaying.value ?: false)
        if (_isPlaying.value == true) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_isPlaying.value == true) {
                delay(1000)
                _time.value = (_time.value ?: 0) - 1
                if (_time.value!! <= 0) {
                    _isPlaying.value = false
                    stopTimer()
                    moveToNextMode()
                    startTimer()
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }



     fun moveToNextMode() {
        _modeCounter = (_modeCounter + 1) % 6 // 6 is the number of modes in the sequence
        when (_modeCounter) {
            0, 2, 4 -> _mode.value = Mode.POMO
            1, 3 -> _mode.value = Mode.SHORT_BREAK
            5 -> _mode.value = Mode.LONG_BREAK
        }
        _time.value = _mode.value!!.duration * 60
         startTimer()
    }

    fun updateModeDuration(mode: Mode, increment: Int) {
        if (mode.duration-1>=1){
            mode.duration += increment
            if (_mode.value == mode) {
                _time.value = mode.duration * 60
            }
        }

    }
    fun setSoundEnabled(enabled: Boolean) {
        localStorage.setSoundInfo(enabled)
        _soundEnabled.value = enabled
    }
}
