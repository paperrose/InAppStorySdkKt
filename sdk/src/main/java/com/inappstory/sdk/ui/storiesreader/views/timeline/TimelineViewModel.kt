package com.inappstory.sdk.ui.storiesreader.views.timeline

import androidx.lifecycle.ViewModel

class TimelineViewModel : ViewModel() {
    fun getProgress(): Float {
        timerRepository?.let {
            return it.getProgress()
        }
        return 0f
    }

    var timerRepository: TimerRepository? = null
}