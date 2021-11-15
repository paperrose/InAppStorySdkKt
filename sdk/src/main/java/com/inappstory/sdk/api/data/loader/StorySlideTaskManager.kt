package com.inappstory.sdk.api.data.loader

import android.util.Log
import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.utils.cache.FileManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.max

class StorySlideTaskManager(var storyTaskManager: StoryTaskManager,
                            val fileManager: FileManager) {
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

    private var loadedStoryPages: HashSet<Pair<String, Int>> = HashSet()

    var secondaryPageTasks: HashMap<Pair<String, Int>, StoryPageTask> =
        HashMap()

    var pageTasks: HashMap<Pair<String, Int>, StoryPageTask> =
        HashMap()

    private fun asyncCheckIndexQueue() {
        GlobalScope.launch { checkIndexQueue() }
    }

    private fun asyncCheckTaskQueue() {
        GlobalScope.launch { checkTaskQueue() }
    }

    private fun checkTaskQueue() {

        var task: StoryPageTask? = null
        var taskKey: Pair<String, Int>? = null
        synchronized(storyPageTaskLock) {
            if (pageTasks.isNotEmpty() || secondaryPageTasks.isNotEmpty()) {
                taskKey = getMaxPriorityTaskKey()
               // Log.e("remove keys", pageTasks.keys.toString())
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
            synchronized(storyPageTaskLock) {
                loadedStoryPages.add(taskKey!!)
                pageTasks.remove(taskKey!!)
                secondaryPageTasks.remove(taskKey!!)
            }
          //  Log.e("remove task key", taskKey.toString())
            asyncCheckTaskQueue()
        } else {
            Thread.sleep(500)
            asyncCheckTaskQueue()
        }
    }

    private fun loadFileByUrl(url: String) {
        val result = fileManager.getFile(url, false)
        result.let {
            Log.e("fileDownload",  "${it.fromCache} ${it.file?.absolutePath}")
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
                        it.slideId, max(2, pageTasks.size+1)
                    )
                    if (created) local.add(it)
                }
            }
            local.forEach {
                neighborsStoriesSlides.remove(it)
            }
            local.clear()
        }
        Thread.sleep(500)
        asyncCheckIndexQueue()
    }

    private val VIDEO = "video"

    private fun createTasks(
        main: Boolean,
        readerStoryDataProtocol: ReaderStoryDataProtocol?,
        slideId: Pair<String, Int>,
        minPriority: Int
    ) : Boolean {
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

    fun clean() {
        synchronized(storyPageTaskLock) {
            pageTasks.clear()
            secondaryPageTasks.clear()
        }
    }

    fun getMaxPriorityTaskKey(): Pair<String, Int>? {
        var key: Pair<String, Int>? = null
        synchronized(storyPageTaskLock) {
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

        }
        return key
    }

    init {
        asyncCheckIndexQueue()
        asyncCheckTaskQueue()
    }
}