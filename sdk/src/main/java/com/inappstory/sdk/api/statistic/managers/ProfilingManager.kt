package com.inappstory.sdk.api.statistic.managers

import com.inappstory.sdk.api.statistic.models.ProfilingTask
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.EmptyCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProfilingManager(private val apiWorker: ApiWorker) {
    private val tasks = ArrayList<ProfilingTask>()
    private val readyTasks = ArrayList<ProfilingTask>()
    private val tasksLock = Any()

    private fun sendTask(task: ProfilingTask, force: Boolean = false) {
        when (task.type) {
            0 -> {
                apiWorker.sendProfilingTiming(
                    task, object : EmptyCallback() {
                        override fun onSuccess(response: Any?) {
                            super.onSuccess(response)
                            if (!force)
                                asyncSendTasks()
                        }

                        override fun onFailure(status: Int, error: String) {
                            super.onFailure(status, error)
                            if (!force) {
                                Thread.sleep(100)
                                asyncSendTasks()
                            }
                        }
                    }
                )
            }
        }
    }

    fun setReady(hash: String, force: Boolean = false) {
        var readyTask: ProfilingTask? = null
        synchronized(tasksLock) {
            tasks.forEach {
                if (it.uniqueHash.equals(hash)) {
                    readyTask = it;
                    return@forEach
                }
            }
            readyTask?.let {
                tasks.remove(readyTask)
            }
        }
        readyTask?.let {
            if (hash.isEmpty()) return
            it.endTime = System.currentTimeMillis()
            if (force) {
                GlobalScope.launch {
                    sendTask(readyTask!!, force = true)
                }
            } else {
                synchronized(tasksLock) {
                    readyTasks.add(readyTask!!)
                }
            }
        }
    }

    fun addTask(
        name: String?,
        hash: String = UUID.randomUUID().toString()
    ): String {
        synchronized(tasksLock) {
            tasks.forEach {
                if (it.uniqueHash.equals(hash)) {
                    it.startTime = System.currentTimeMillis()
                    return hash
                }
            }
            tasks.add(
                ProfilingTask(
                    hash,
                    name,
                    startTime = System.currentTimeMillis()
                )
            )
        }
        return hash
    }

    private fun sendTasks() {
        var task: ProfilingTask? = null
        synchronized(tasksLock) {
            if (readyTasks.isEmpty()) {
                return@synchronized
            }
            task = readyTasks.removeAt(0)
        }
        if (task != null) {
            sendTask(task!!)
        } else {
            Thread.sleep(100)
            asyncSendTasks()
        }
    }

    private fun asyncSendTasks() {
        GlobalScope.launch {
            sendTasks()
        }
    }

    init {
        asyncSendTasks()
    }
}