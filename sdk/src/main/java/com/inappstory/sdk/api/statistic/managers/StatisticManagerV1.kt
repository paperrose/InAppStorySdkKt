package com.inappstory.sdk.api.statistic.managers

import com.inappstory.sdk.api.data.models.StatisticSessionObject
import com.inappstory.sdk.api.statistic.models.StatisticTaskV1
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.EmptyCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID.randomUUID
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StatisticManagerV1(var apiWorker: ApiWorker) {
    private val tasks = HashMap<String, StatisticTaskV1>()
    private val tasksLock = Any()
    private var eventCount = 0
    private var eventCountLock = Any()

    fun startSlide(storyId: String, slideIndex: Int): String {
        val task = StatisticTaskV1(
            startTime = System.currentTimeMillis(),
            type = 1,
            storyId = storyId,
            slideIndex = slideIndex
        )
        val hash = randomUUID().toString()
        tasks[hash] = task
        return hash
    }

    fun addPreview(vararg slides: Int, force: Boolean = false) {
        if (force) {
            sendTask(
                StatisticTaskV1(
                    type = 5,
                    slides = slides.asList()
                )
            )
        } else {
            synchronized(tasksLock) {
                val task = StatisticTaskV1(
                    type = 5,
                    slides = slides.asList()
                )
                tasks[randomUUID().toString()] = task
            }

        }
    }

    fun endSlide(
        uid: String,
        click: Boolean = false,
        deeplink: Boolean = false
    ) {
        synchronized(tasksLock) {
            val task = tasks[uid]
            task?.let {
                if (click) it.type = 2
                it.endTime = System.currentTimeMillis()
                if (deeplink) it.endTime = it.startTime

            }
        }
    }


    private fun sendTasks() {
        val taskData = ArrayList<ArrayList<String>>()
        synchronized(tasksLock) {
            tasks.keys.forEach {
                taskData.add(generateTask(tasks[it]!!))
            }
            tasks.clear()
        }
        if (taskData.isNotEmpty())
            apiWorker.sendSessionStatistic(StatisticSessionObject(taskData),
                object : EmptyCallback() {

                }
            )
    }

    private fun generateTask(task: StatisticTaskV1): ArrayList<String> {
        val res = ArrayList<String>()
        if (task.type!! == 5) {
            task.slides!!.forEach {
                res.add(it.toString())
            }
        } else {
            res.add(task.storyId!!)
            res.add("${task.slideIndex!!}")
            res.add("${task.endTime!! - task.startTime!!}")
        }
        res.add(0, "${task.type!!}")
        synchronized(eventCountLock) {
            res.add(0, "${eventCount++}")
        }
        return res
    }

    private fun sendTask(task: StatisticTaskV1) {
        val taskData = ArrayList<ArrayList<String>>()
        taskData.add(generateTask(task))
        GlobalScope.launch {
            apiWorker.sendSessionStatistic(StatisticSessionObject(taskData),
                object : EmptyCallback() {

                }
            )
        }
    }


    private fun asyncSendStat() {
        GlobalScope.launch {
            Thread.sleep(30000)
            asyncSendStat()
        }
        GlobalScope.launch { sendTasks() }
    }

    init {
        asyncSendStat()
    }
}