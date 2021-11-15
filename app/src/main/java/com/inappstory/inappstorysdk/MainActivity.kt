package com.inappstory.inappstorysdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inappstory.sdk.api.data.loader.*
import com.inappstory.sdk.api.data.models.protocols.CachedStoryModelProtocol
import com.inappstory.sdk.api.data.storage.StoriesStorage
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.GetStoriesListCallback
import com.inappstory.sdk.utils.cache.FileManager
import com.inappstory.sdk.utils.file.FileSystemWorker
import com.inappstory.sdk.utils.cache.LruCacheManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cacheManager = LruCacheManager(FileSystemWorker(), cacheDir)
        val fileManager = FileManager(cacheManager)
        val storiesStorage = StoriesStorage(cacheManager)
        val c = storiesStorage.getStory("4050")
        val apiWorker = ApiWorker(this, "ImsXI54EwNNho8nvV9TIIWgOYZc9sKOQ",
            null, "https://api.inappstory.com/", "test")
        val storyTaskManager = StoryTaskManager(apiWorker)
        val storyPageTaskManager = StorySlideTaskManager(storyTaskManager, fileManager)
        apiWorker.getStoriesList(null, object : GetStoriesListCallback() {
            override fun onSuccess(response: Any?) {
                val storiesList = response as ArrayList<CachedStoryModelProtocol>
                val mainStoryCallback = LoadStoryTaskWithCallback(null, storiesList[1].id())
                val neighborStoriesCallbacks = ArrayList<LoadStoryTaskWithCallback>()
                neighborStoriesCallbacks
                    .add(LoadStoryTaskWithCallback(null, storiesList[0].id()))
                neighborStoriesCallbacks
                    .add(LoadStoryTaskWithCallback(null, storiesList[2].id()))
                val mainSlideCallback = LoadSlideTaskWithCallback(null,
                    Pair(storiesList[1].id(), 0))
                val neighborSlidesCallbacks = ArrayList<LoadSlideTaskWithCallback>()
                neighborSlidesCallbacks
                    .add(LoadSlideTaskWithCallback(null, Pair(storiesList[0].id(), 0)))
                neighborSlidesCallbacks
                    .add(LoadSlideTaskWithCallback(null, Pair(storiesList[2].id(), 0)))
                storyTaskManager.loadStory(mainStoryCallback, neighborStoriesCallbacks, false)
                storyPageTaskManager.loadSlides(mainSlideCallback, neighborSlidesCallbacks, false)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }

        })
    }
}