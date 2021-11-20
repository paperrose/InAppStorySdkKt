package com.inappstory.sdk.api.dispatchers

import android.util.Log
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.EmptyCallback

class StoryFavoriteDispatcher(var apiWorker: ApiWorker? = null) : Dispatcher() {
    fun favStory(storyId: String, value: String, callback: ((fav: String) -> Unit)? = null) {

        apiWorker?.storyFavorite(storyId, value, object : EmptyCallback() {
            override fun onSuccess(response: Any?) {
                Log.e("click", storyId)
                callback?.invoke(value)
                success(arrayOf(DispatcherMessages.STORY_FAVORITE, storyId, value))
            }

            override fun onFailure(status: Int, error: String) {
                error(arrayOf(DispatcherMessages.STORY_FAVORITE, storyId, value))
            }
        })
    }
}