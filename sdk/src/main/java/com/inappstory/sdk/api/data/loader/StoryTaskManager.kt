package com.inappstory.sdk.api.data.loader

import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.GetStoryCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors

class StoryTaskManager(
    val apiWorker: ApiWorker
) {
    private val storyTaskLock: Any = Object()
    var cleaned = false
    var loadedStories: HashMap<String, ReaderStoryDataProtocol> = HashMap()
    private var storyTasks: HashMap<String, StoryTask> =
        HashMap()
    private var callbacks: HashMap<String, OnStoryLoaded?> =
        HashMap()
    private val loader = Executors.newFixedThreadPool(1)

    fun getLoadedStory(storyId: String): ReaderStoryDataProtocol? {
        synchronized(storyTaskLock) {
            return loadedStories[storyId]
        }
    }

    val runnable: Runnable = Runnable {
        checkQueue()
    }

    private fun asyncCheckQueue() {
        loader.submit(runnable)
       // GlobalScope.launch { checkQueue() }
    }

    private fun checkQueue() {
        synchronized(storyTaskLock) {
            if (cleaned) return
        }
        var loadId: String? = null
        synchronized(storyTaskLock) {
            val key = getMaxPriorityKey()
            if (!loadedStories.containsKey(key)) {
                loadId = key
            }
            storyTasks.remove(key)
        }
        if (loadId == null) {
            Thread.sleep(100)
            synchronized(storyTaskLock) {
                if (cleaned) return
            }
            asyncCheckQueue()
        } else {
            apiWorker.getStoryById(loadId!!, object : GetStoryCallback() {
                override fun isAsync(): Boolean {
                    return true
                }

                override fun onSuccess(response: Any?) {

                    asyncCheckQueue()
                    synchronized(storyTaskLock) {
                        loadedStories[loadId!!] = response as ReaderStoryDataProtocol
                        callbacks[loadId!!]?.onStoryLoaded(response)
                    }
                }

                override fun onFailure(status: Int, error: String) {
                    asyncCheckQueue()
                    synchronized(storyTaskLock) {
                        callbacks[loadId!!]?.onStoryError()
                    }
                }
            })
        }
    }

    fun loadStory(
        mainStoryTask: LoadStoryTaskWithCallback,
        neighborsTasks: ArrayList<LoadStoryTaskWithCallback>,
        reload: Boolean
    ) {
        var priority = 0
        synchronized(storyTaskLock) {
            storyTasks.clear()
            if (reload) loadedStories.remove(mainStoryTask.storyId)
            storyTasks[mainStoryTask.storyId] = StoryTask(priority++)
            callbacks[mainStoryTask.storyId] = mainStoryTask.onStoryLoaded
            neighborsTasks.forEach {
                if (reload) loadedStories.remove(it.storyId)
                storyTasks[it.storyId] = StoryTask(priority++)
                callbacks[it.storyId] = it.onStoryLoaded
            }
        }
    }

    fun clean() {
        synchronized(storyTaskLock) {
            cleaned = true
            callbacks.clear()
            loadedStories.clear()
            storyTasks.clear()
        }
    }

    fun getMaxPriorityKey(): String? {
        var key: String? = null
        synchronized(storyTaskLock) {
            var priority = 10
            storyTasks.keys.forEach {
                if (storyTasks[it]!!.priority < priority) {
                    priority = storyTasks[it]!!.priority
                    key = it
                }
            }
        }
        return key
    }


    init {
        asyncCheckQueue()
    }
}