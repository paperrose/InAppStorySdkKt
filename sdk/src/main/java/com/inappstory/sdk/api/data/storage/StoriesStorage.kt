package com.inappstory.sdk.api.data.storage

import com.inappstory.sdk.api.data.models.CachedStoryData
import com.inappstory.sdk.api.data.models.StoryData
import com.inappstory.sdk.api.data.models.protocols.CachedStoryModelProtocol
import com.inappstory.sdk.utils.cache.LruCacheManager
import com.inappstory.sdk.utils.json.JsonParser

class StoriesStorage(private val cacheManager: LruCacheManager) {

    private val stories: HashMap<String, CachedStoryModelProtocol> = HashMap()

    fun putStory(storyData: CachedStoryModelProtocol?) {
        if (storyData == null) return
        stories[storyData.id()] = storyData
        saveToFileCache(storyData as CachedStoryData)
    }

    fun putStories(stories: ArrayList<CachedStoryModelProtocol>) {
        stories.forEach {
            putStory(it)
        }
    }

    private fun saveToFileCache(storyData: CachedStoryData) {
        cacheManager.modelsCache?.putModel(
            storyData.id(),
            JsonParser().getJson(storyData)!!
        )
    }

    fun getStory(storyId: String): CachedStoryModelProtocol? {
        var story: CachedStoryModelProtocol? = stories[storyId];
        if (story == null) {
            story = JsonParser().jsonToPOJO(
                cacheManager.modelsCache?.getModel(storyId),
                CachedStoryData::class, false
            ) as CachedStoryData?
        }
        return story
    }

    fun updateFavoriteStatus(storyId: String, favorite: Boolean) {
        val localData: CachedStoryData? = stories[storyId] as CachedStoryData
        localData?.let {
            it.favorite = favorite
            saveToFileCache(it)
        }
    }

    fun updateLikeStatus(storyId: String, like: Int) {
        val localData: CachedStoryData? = stories[storyId] as CachedStoryData
        localData?.let {
            it.like = like
            saveToFileCache(it)
        }
    }

    fun updateStory(storyData: CachedStoryModelProtocol?) {
        if (storyData == null) return
        val localData: CachedStoryData? = stories[storyData.id()] as CachedStoryData?
        if (localData == null) {
            putStory(storyData)
            return
        }
        when {
            storyData.like() -> localData.like = 1
            storyData.dislike() -> localData.like = -1
            else -> localData.like = 0
        }
        localData.backgroundColor = storyData.backgroundColor()
        localData.deeplink = storyData.deeplink()
        localData.disableClose = storyData.disableClose()
        localData.titleColor = storyData.titleColor()
        localData.favorite = storyData.favorite()
        localData.hasAudio = storyData.hasAudio()
        localData.hasFavorite = storyData.hasFavorite()
        localData.hasLike = storyData.hasLike()
        localData.hasShare = storyData.hasShare()
        saveToFileCache(localData)

    }
}