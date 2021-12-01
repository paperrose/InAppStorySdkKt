package com.inappstory.sdk.ui.list

import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.dispatchers.DispatcherMessages
import com.inappstory.sdk.api.dispatchers.DispatcherSubscriber
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.ThreadNavigator

class StoriesListManager : DispatcherSubscriber {
    var adapter: StoriesAdapter? = null
    fun storyFavorite(storyId: String, favorite: String) {
        ThreadNavigator.runInUiThread {
            adapter?.let {
                if (favorite == "1") {
                    it.stories.forEach { story ->
                        if (story.id() == storyId) {
                            it.addFavorite(
                                FavoriteImage(
                                    storyId,
                                    story.images(),
                                    story.backgroundColor()
                                )
                            )
                            return@runInUiThread
                        }
                    }
                } else {
                    it.removeFavorite(storyId)
                }
            }
        }
    }

    fun storyOpen(storyId: String) {

    }

    fun changeUser() {

    }

    override fun notify(message: Array<Any?>?) {
        message?.let {
            when {
                it[0] == DispatcherMessages.STORY_FAVORITE -> {
                    storyFavorite(it[1] as String, it[2] as String)
                }
            }
        }

    }

    override fun error(message: Array<Any?>?) {
        message?.let {
            when {
                it[0] == DispatcherMessages.STORY_FAVORITE -> {
                }
            }
        }
    }

    override fun subscribe() {
        InAppStoryManager.storyFavoriteDispatcher.subscribers.add(this)
    }

    override fun unsubscribe() {
        InAppStoryManager.storyFavoriteDispatcher.subscribers.remove(this)
    }
}