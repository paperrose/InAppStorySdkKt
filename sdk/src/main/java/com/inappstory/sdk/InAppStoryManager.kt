package com.inappstory.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import com.inappstory.sdk.api.dispatchers.StoryFavoriteDispatcher
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.utils.cache.LruCacheManager
import com.inappstory.sdk.utils.imageloader.ImageLoader

@SuppressLint("StaticFieldLeak")
object InAppStoryManager {
    var uId: String? = null
    fun setUserId(uId: String?) {
        if (uId.isNullOrEmpty()) return
        this.uId = uId
    }
    var context: Context? = null
    var apiWorker: ApiWorker? = null
    var cacheManager: LruCacheManager? = null
    var imageLoader = ImageLoader()
    var storyFavoriteDispatcher: StoryFavoriteDispatcher? = null
}