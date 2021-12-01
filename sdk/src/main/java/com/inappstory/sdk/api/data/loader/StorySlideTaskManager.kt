package com.inappstory.sdk.api.data.loader

import android.util.Log
import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.utils.cache.FileManager
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.max

class StorySlideTaskManager(
    var storyTaskManager: StoryTaskManager,
    val fileManager: FileManager
) {
    private val storyPageTaskLock: Any = Object()

    class StoryPageTask(
        var priority: Int,
        var urls: ArrayList<String>,
        var videoUrls: ArrayList<String>
    ) {
        override fun toString(): String {
            return "$priority"
        }
    }


    private val indexLoader = Executors.newFixedThreadPool(1)


    private val slideLoader = Executors.newFixedThreadPool(1)

    val indexRunnable: Runnable = Runnable {
        checkIndexQueue()
    }
    val slideRunnable: Runnable = Runnable {
        checkTaskQueue()
    }
    private var loadedStoryPages: HashSet<Pair<String, Int>> = HashSet()

    var secondaryPageTasks: HashMap<Pair<String, Int>, StoryPageTask> =
        HashMap()

    var pageTasks: HashMap<Pair<String, Int>, StoryPageTask> =
        HashMap()

    private var callbacks: HashMap<Pair<String, Int>, OnSlideLoaded?> =
        HashMap()

    private fun asyncCheckIndexQueue() {
        indexLoader.submit(indexRunnable)
    }

    private fun asyncCheckTaskQueue() {
        slideLoader.submit(slideRunnable)
    }

    private fun checkTaskQueue() {

        var task: StoryPageTask? = null
        var taskKey: Pair<String, Int>? = null
        synchronized(storyPageTaskLock) {

            if (pageTasks.isNotEmpty() || secondaryPageTasks.isNotEmpty()) {
                taskKey = getMaxPriorityTaskKey()
                if (taskKey != null) {
                    if (pageTasks.containsKey(taskKey)) {
                        task = pageTasks[taskKey]
                    } else if (secondaryPageTasks.containsKey(taskKey)) {
                        task = secondaryPageTasks[taskKey]
                    }

                }
                //   Log.e("remove keys2", pageTasks.keys.toString())
            }
        }
        if (task != null) {
            task?.urls?.forEach {
                loadFileByUrl(it)
            }
            task?.videoUrls?.forEach {
                loadFileByUrl(it)
            }
            var callback: OnSlideLoaded? = null
            synchronized(storyPageTaskLock) {
                taskKey?.let { key ->
                    loadedStoryPages.add(key)
                    callback = callbacks[key]
                    callbacks.remove(key)
                    pageTasks.remove(key)
                    secondaryPageTasks.remove(key)
                }
            }
            taskKey?.let { key ->
                callback?.onSlideLoaded(
                    key.first,
                    key.second
                )
            }
            //  Log.e("remove task key", taskKey.toString())
            asyncCheckTaskQueue()
        } else {
            Thread.sleep(100)
            asyncCheckTaskQueue()
        }
    }

    private fun loadFileByUrl(url: String) {
        val result = fileManager.getFile(url, false)
        result.let {
            Log.e("fileDownload", "${it.fromCache} ${it.file?.absolutePath}")
        }
    }

    private fun checkIndexQueue() {
        synchronized(storyPageTaskLock) {
            if (mainStorySlide != null) {
                val id = mainStorySlide!!.slideId
                val created = createTasks(true, storyTaskManager.loadedStories[id.first], id, 0)
                if (created) mainStorySlide = null
            }
            var local = ArrayList<LoadSlideTaskWithCallback>()
            neighborsStoriesSlides.forEach {
                if (!loadedStoryPages.contains(it.slideId)) {
                    val created = createTasks(
                        false, storyTaskManager.loadedStories[it.slideId.first],
                        it.slideId, max(2, pageTasks.size + 1)
                    )
                    if (created) local.add(it)
                }
            }
            local.forEach {
                neighborsStoriesSlides.remove(it)
            }
            local.clear()
        }
        Thread.sleep(100)
        asyncCheckIndexQueue()
    }

    private val VIDEO = "video"

    private fun createTasks(
        main: Boolean,
        readerStoryDataProtocol: ReaderStoryDataProtocol?,
        slideId: Pair<String, Int>,
        minPriority: Int
    ): Boolean {

        if (readerStoryDataProtocol == null) return false
        var priority = minPriority
        val slidesCount = readerStoryDataProtocol.slidesCount()
        val index = slideId.second
        if (main) {
            for (i in 0 until slidesCount) {
                if (i == index || i == index + 1) {
                    pageTasks[Pair(slideId.first, i)] = StoryPageTask(
                        priority++,
                        readerStoryDataProtocol.srcListUrls(i, null),
                        readerStoryDataProtocol.srcListUrls(i, VIDEO)
                    )
                } else {
                    secondaryPageTasks[Pair(slideId.first, i)] = StoryPageTask(
                        i,
                        readerStoryDataProtocol.srcListUrls(i, null),
                        readerStoryDataProtocol.srcListUrls(i, VIDEO)
                    )
                }
            }
        } else {
            for (i in 0 until slidesCount) {
                if (i == index || i == index + 1) {
                    pageTasks[Pair(slideId.first, i)] = StoryPageTask(
                        priority++,
                        readerStoryDataProtocol.srcListUrls(i, null),
                        readerStoryDataProtocol.srcListUrls(i, VIDEO)
                    )
                }
            }
        }
        return true
    }

    var mainStorySlide: LoadSlideTaskWithCallback? = null
    var neighborsStoriesSlides: ArrayList<LoadSlideTaskWithCallback> = ArrayList()

    fun loadSlides(
        mainStorySlide: LoadSlideTaskWithCallback,
        neighborsStoriesSlides: ArrayList<LoadSlideTaskWithCallback>,
        reload: Boolean
    ) {

        synchronized(storyPageTaskLock) {
            pageTasks.clear()
            secondaryPageTasks.clear()
            this.mainStorySlide = mainStorySlide
            this.neighborsStoriesSlides.clear()
            this.neighborsStoriesSlides.addAll(neighborsStoriesSlides)
        }
    }

    fun hasSlide(slide: Pair<String, Int>): Boolean {
        synchronized(storyPageTaskLock) {
            return loadedStoryPages.contains(slide)
        }
    }

    fun addCallback(slide: Pair<String, Int>, onSlideLoaded: OnSlideLoaded) {
        synchronized(storyPageTaskLock) {
            callbacks[slide] = onSlideLoaded
        }
    }

    fun clean() {
        synchronized(storyPageTaskLock) {
            pageTasks.clear()
            secondaryPageTasks.clear()
        }
    }

    fun getMaxPriorityTaskKey(): Pair<String, Int>? {
        var key: Pair<String, Int>? = null
        var priority = 100

        pageTasks.forEach {
            if (it.value.priority < priority) {
                priority = it.value.priority
                key = it.key
            }
        }
        if (key == null) {
            secondaryPageTasks.forEach {
                if (it.value.priority <= priority) {
                    priority = it.value.priority
                    key = it.key
                }
            }
        }


        return key
    }

    init {
        asyncCheckIndexQueue()
        asyncCheckTaskQueue()
    }
}