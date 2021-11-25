package com.inappstory.sdk.ui.storiesreader.views.timeline


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.math.max

class TimerRepository {
    private val durations = ArrayList<Long>()
    private var leftDuration: Long = -1
    private var startTimer: Long = 0
    private var totalTime: Long = 0
    private var currentIndex: Int = 0
    private var sendData = MutableLiveData<Pair<Int, Long>>()

    fun getDurationsCount(): Int = durations.size

    fun getObservableData(): MutableLiveData<Pair<Int, Long>> {
        return sendData
    }

    fun getProgress(): Float {
        if (durations[currentIndex] == 0L || startTimer == 0L) return 0f
        val timePassed = System.currentTimeMillis() - startTimer
        val leftTime = max(leftDuration - timePassed, 0L)
        return 1f - leftTime.toFloat() / durations[currentIndex]

    }

    fun setDurations(durations: ArrayList<Long>) {
        this.durations.clear()
        this.durations.addAll(durations)
    }

    fun resumeTimer(totalTime: Long? = null) {
        startTimer = System.currentTimeMillis()
        totalTime?.let {
            this.totalTime = it
        }
    }

    fun stopTimer(removeObserver: Boolean = false) {
        val timePassed = System.currentTimeMillis() - startTimer
        totalTime += timePassed
        sendData.postValue(Pair(currentIndex, totalTime))
        if (removeObserver) {
            clearObserver()
        }
    }

    fun pauseTimer() {
        if (leftDuration > 0) {
            val timePassed = System.currentTimeMillis() - startTimer
            leftDuration -= timePassed
            totalTime += timePassed
        }
    }

    private fun clearObserver() {
        if (localObservable.hasActiveObservers()) localObservable.removeObserver(localObserver)
    }

    private val localObservable: MutableLiveData<Any> = MutableLiveData()
    private val localObserver = Observer<Any> {
        stopTimer()
    }

    fun startTimer(duration: Long? = null, index: Int = 0) {
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