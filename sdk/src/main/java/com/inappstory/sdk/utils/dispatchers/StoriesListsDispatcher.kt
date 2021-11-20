package com.inappstory.sdk.utils.dispatchers

import com.inappstory.sdk.ui.list.StoriesListManager

object StoriesListsDispatcher {
    private val subscribers = ArrayList<StoriesListManager>()

    fun addSubscriber(manager: StoriesListManager) {
        subscribers.add(manager)
    }
    fun removeSubscriber(manager: StoriesListManager) {
        subscribers.remove(manager)
    }

    fun storyFavorite(storyId: String, favorite: String) {
        subscribers.forEach {
            it.storyFavorite(storyId, favorite)
        }
    }

    fun storyOpen(storyId: String) {
        subscribers.forEach {
            it.storyOpen(storyId)
        }
    }

    fun changeUser() {
        subscribers.forEach {
            it.changeUser()
        }
    }
}