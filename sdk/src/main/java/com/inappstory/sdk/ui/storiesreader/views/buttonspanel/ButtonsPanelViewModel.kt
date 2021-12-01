package com.inappstory.sdk.ui.storiesreader.views.buttonspanel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.dispatchers.DispatcherMessages
import com.inappstory.sdk.api.dispatchers.DispatcherSubscriber

class ButtonsPanelViewModel : ViewModel(), DispatcherSubscriber {
    private val likeObservable: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()
    private val favoriteObservable: MutableLiveData<Pair<Boolean, Boolean>> = MutableLiveData()
    var storyId: String? = null

    fun likeObservable(): MutableLiveData<Pair<Int, Boolean>> {
        return likeObservable
    }

    fun favoriteObservable(): MutableLiveData<Pair<Boolean, Boolean>> {
        return favoriteObservable
    }

    fun initValues(favorite: String, like: String) {
        likeObservable.postValue(Pair(like.toInt(), true))
        favoriteObservable.postValue(Pair(favorite == "1", true))
    }

    override fun notify(message: Array<Any?>?) {
        message?.let {
            if (it[1] == storyId) {
                when (it[0]) {
                    DispatcherMessages.STORY_FAVORITE -> {
                        storyFavorited(it[2] as String, true)
                    }

                    DispatcherMessages.STORY_LIKE -> {
                        storyLiked(it[2] as String, true)
                    }
                }
            }
        }
    }

    fun storyFavorite() {
        var favorite = "1"
        if (favoriteObservable.value!!.first)
            favorite = "0"
        InAppStoryManager.storyFavoriteDispatcher.favStory(storyId!!, favorite)
    }

    fun storyLike() {
        var like = "0"
        if (likeObservable.value!!.first != 1)
            like = "1"
        InAppStoryManager.storyLikeDispatcher.likeDislikeStory(storyId!!, like)
    }

    fun storyDislike() {
        var dislike = "0"
        if (likeObservable.value!!.first != -1)
            dislike = "-1"
        InAppStoryManager.storyLikeDispatcher.likeDislikeStory(storyId!!, dislike)
    }

    private fun storyFavorited(favorite: String, success: Boolean) {
        favoriteObservable.postValue(Pair(favorite == "1", success))
    }

    private fun storyLiked(like: String, success: Boolean) {
        likeObservable.postValue(Pair(like.toInt(), success))
    }

    override fun error(message: Array<Any?>?) {
        message?.let {
            if (it[1] == storyId) {
                when (it[0]) {
                    DispatcherMessages.STORY_FAVORITE -> {
                        storyFavorited(favoriteObservable.value!!.first.toString(), false)
                    }

                    DispatcherMessages.STORY_LIKE -> {
                        storyLiked(likeObservable.value!!.first.toString(), false)
                    }
                }
            }
        }
    }

    override fun subscribe() {
        InAppStoryManager.storyFavoriteDispatcher.subscribers.add(this)
        InAppStoryManager.storyLikeDispatcher.subscribers.add(this)
    }

    override fun unsubscribe() {
        InAppStoryManager.storyFavoriteDispatcher.subscribers.remove(this)
        InAppStoryManager.storyLikeDispatcher.subscribers.remove(this)
    }

    init {
    }

    fun destroy() {
    }
}