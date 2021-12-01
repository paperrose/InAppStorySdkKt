package com.inappstory.sdk.ui.storiesreader.views.timeline

import androidx.lifecycle.ViewModel

class TimelineViewModel : ViewModel() {
    fun getProgress(): Float {
        if (!isActive) return 0f
        storiesTimer?.let {
            return it.getProgress()
        }
        return 0f
    }

    var isActive: Boolean = false

    var storiesTimer: StoriesTimer? = null

    fun startTimer(duration: Long? = null, index: Int = 0) {
        if (!isActive) return
        storiesTimer?.startTimer(duration, index)
    }

    fun restartTimer(duration: Long? = null, index: Int = 0) {
        if (!isActive) return
        clearTimer()
        startTimer(duration, index)
    }

    fun clearTimer() {
        storiesTimer?.clearTimer()
    }

    fun stopTimer() {
        storiesTimer?.stopTimer()
    }
}