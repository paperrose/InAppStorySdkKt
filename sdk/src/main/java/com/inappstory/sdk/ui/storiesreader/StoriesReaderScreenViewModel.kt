package com.inappstory.sdk.ui.storiesreader

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.data.loader.*
import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.api.data.storage.StoriesStorage
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.utils.cache.FileManager
import com.inappstory.sdk.utils.common.ThreadNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

class StoriesReaderScreenViewModel() : ViewModel() {
    private var apiWorker: ApiWorker? = null
    private var fileManager: FileManager? = null
    private var storyTaskManager: StoryTaskManager? = null
    private var storyPageTaskManager: StorySlideTaskManager? = null
    private var storiesIds: ArrayList<String> = ArrayList()
    private var openedIndexes: HashMap<String, Int> = HashMap()
    var currentStoryIndex = MutableLiveData<Int>()

    fun getStoryIndex(storyId: String): Int {
        return storiesIds.indexOf(storyId)
    }

    fun getLastIndex(storyId: String): Int {
        openedIndexes[storyId]?.let { return it }
        return 0
    }

    fun notify(message: Array<Any?>) {
        when (message[0]) {

        }
    }

    fun setLastIndex(storyId: String, index: Int) {
        openedIndexes[storyId] = index
    }

    fun initManagers(storiesIds: ArrayList<String>) {
        InAppStoryManager.apiWorker?.let { apiWorker ->
            this.apiWorker = apiWorker
            InAppStoryManager.cacheManager?.let {
                this.storiesIds.clear()
                this.storiesIds.addAll(storiesIds)
                fileManager = FileManager(it)
                storyTaskManager = StoryTaskManager(apiWorker)
                storyPageTaskManager = StorySlideTaskManager(storyTaskManager!!, fileManager!!)
            }
        }
    }

    fun setCurrentSlideDownload(storyId: String, index: Int) {
        storyPageTaskManager?.let {
            val ind = storiesIds.indexOf(storyId)
            if (ind < 0) return
            val mainSlideCallback = LoadSlideTaskWithCallback(
                null,
                Pair(storyId, getLastIndex(storyId))
            )
            val neighborSlidesCallbacks = ArrayList<LoadSlideTaskWithCallback>()
            if (ind > 0)
                neighborSlidesCallbacks
                    .add(
                        LoadSlideTaskWithCallback(
                            null, Pair(
                                storiesIds[ind - 1],
                                getLastIndex(storiesIds[ind - 1])
                            )
                        )
                    )
            if (ind + 1 < storiesIds.size)
                neighborSlidesCallbacks
                    .add(
                        LoadSlideTaskWithCallback(
                            null, Pair(
                                storiesIds[ind + 1],
                                getLastIndex(storiesIds[ind + 1])
                            )
                        )
                    )
            it.loadSlides(mainSlideCallback, neighborSlidesCallbacks, false)
        }
    }

    fun getSlide(storyId: String, index: Int, onSlideLoaded: OnSlideLoaded) {
        storyPageTaskManager?.let {
            if (it.hasSlide(Pair(storyId, index))) {

                Log.e("slideData_hasSlides", "${Pair(storyId, index)}")
                onSlideLoaded.onSlideLoaded(storyId, index)
            } else {
                it.addCallback(Pair(storyId, index), onSlideLoaded)
            }
        }
    }

    fun getStory(storyId: String): ReaderStoryDataProtocol? {
        storyTaskManager?.let {
            return it.getLoadedStory(storyId)
        }
        return null
    }

    fun getCurrentIdLiveData(): MutableLiveData<String> {
        return currentIdData
    }

    fun setCurrentIdLiveData(storyId: String) {
        return currentIdData.postValue(storyId)
    }

    private val currentIdData = MutableLiveData<String>()

    fun getStory(storyId: String, subscriber: MutableLiveData<ReaderStoryDataProtocol>) {
        storyTaskManager?.let {
            it.getLoadedStory(storyId)?.let { story ->
                subscriber.postValue(story)
                return
            }
        }
        subscribers[storyId] = subscriber
    }

    val subscribers = HashMap<String, MutableLiveData<ReaderStoryDataProtocol>>()

    fun loadStory(ind: Int, storyLoaded: OnStoryLoaded? = null) {
        storyTaskManager?.let {
            val onStoryLoaded = storyLoaded
                ?: object : OnStoryLoaded {
                    override fun onStoryLoaded(story: ReaderStoryDataProtocol) {
                        subscribers[story.id()]?.postValue(story)
                        InAppStoryManager.storiesStorage?.updateStory(story)
                    }

                    override fun onStoryError() {

                    }
                }
            val mainStoryCallback = LoadStoryTaskWithCallback(onStoryLoaded, storiesIds[ind])
            val neighborStoriesCallbacks = ArrayList<LoadStoryTaskWithCallback>()
            if (ind > 0)
                neighborStoriesCallbacks
                    .add(LoadStoryTaskWithCallback(onStoryLoaded, storiesIds[ind - 1]))
            if (ind + 1 < storiesIds.size)
                neighborStoriesCallbacks
                    .add(LoadStoryTaskWithCallback(onStoryLoaded, storiesIds[ind + 1]))

            it.loadStory(mainStoryCallback, neighborStoriesCallbacks, false)
        }
    }

    fun loadStory(storyId: String) {
        val ind = storiesIds.indexOf(storyId)
        if (ind < 0) return
        loadStory(ind)
    }
}