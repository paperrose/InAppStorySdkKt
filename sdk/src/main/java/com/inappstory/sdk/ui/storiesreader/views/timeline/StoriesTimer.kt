package com.inappstory.sdk.ui.storiesreader.views.timeline


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

class StoriesTimer {
    private val durations = ArrayList<Long>()
    private var leftDuration: Long = -1
    private var startTimer: Long = 0
    private var totalTime: Long = 0
    private var currentIndex: Int = 0
    private var sendData = MutableLiveData<Pair<Int, Long>>()
    private var lastProgress = 0f
    private var paused = false
    fun getDurationsCount(): Int = durations.size

    fun getObservableData(): MutableLiveData<Pair<Int, Long>> {
        return sendData
    }

    fun getProgress(): Float {

        synchronized(syncLock) {
            if (durations[currentIndex] == 0L || startTimer == 0L) return 0f
            if (!isActive || paused) return lastProgress
            val timePassed = System.currentTimeMillis() - startTimer
            val leftTime = max(leftDuration - timePassed, 0L)
            lastProgress = 1f - leftTime.toFloat() / durations[currentIndex]
            return lastProgress
        }
    }

    fun destroy() {
        isActive = false
    }

    val syncLock = Any()

    private fun checkTimer() {
        synchronized(syncLock) {
            if (startTimer == 0L) return
            val timePassed = System.currentTimeMillis() - startTimer
            if (leftDuration - timePassed <= 0) {
                stopTimer()
            }
        }
    }

    fun setDurations(durations: ArrayList<Long>) {
        this.durations.clear()
        this.durations.addAll(durations)
    }

    fun clearTimer() {
        synchronized(syncLock) {
            startTimer = 0
            paused = false
            lastProgress = 0f
        }
    }

    fun stopTimer() {
        val timePassed = System.currentTimeMillis() - startTimer
        totalTime += timePassed
        sendData.postValue(Pair(currentIndex, totalTime))
    }

    fun pauseTimer() {
        if (leftDuration > 0) {
            paused = true
            val timePassed = System.currentTimeMillis() - startTimer
            leftDuration -= timePassed
            leftDuration = max(leftDuration, 0L)
            totalTime += timePassed
        }
    }

    fun resumeTimer(totalTime: Long? = null) {
        synchronized(syncLock) {
            startTimer = System.currentTimeMillis()
            totalTime?.let {
                this.totalTime = it
            }
            paused = false
        }
    }

    fun startTimer(duration: Long? = null, index: Int = 0) {
        synchronized(syncLock) {
            var timerDuration = durations[index]
            duration?.let {
                timerDuration = it
            }
            leftDuration = timerDuration
            currentIndex = index
            totalTime = 0
            startTimer = System.currentTimeMillis()
        }
    }

    var isActive = true
    var activeTask = repeatableCheck()

    private fun repeatableCheck(): Job {
        return GlobalScope.launch {
            while (isActive) {
                checkTimer()
                delay(20)
            }
        }
    }

}