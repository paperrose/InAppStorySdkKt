package com.inappstory.sdk.network

import android.content.Context
import android.util.Log
import com.inappstory.sdk.api.data.models.StatisticSessionObject
import com.inappstory.sdk.api.statistic.managers.ProfilingManager
import com.inappstory.sdk.api.statistic.managers.StatisticManagerV1
import com.inappstory.sdk.api.statistic.managers.StatisticManagerV2
import com.inappstory.sdk.api.statistic.models.ProfilingTask
import com.inappstory.sdk.api.statistic.models.StatisticTaskV2
import com.inappstory.sdk.network.callbacks.*
import com.inappstory.sdk.network.utils.NetworkSettings
import com.inappstory.sdk.utils.imageloader.ImageLoader

class ApiWorker(
    var context: Context, var apiKey: String,
    var testKey: String?, var cmsUrl: String,
    var userId: String?
) {
    val requestGenerator = RequestGenerator(context, NetworkSettings(apiKey, testKey, cmsUrl), userId)
    val sessionManager = SessionManager(requestGenerator)
    val statisticManagerV2 = StatisticManagerV2(this)
    val statisticManagerV1 = StatisticManagerV1(this)
    val profilingManager = ProfilingManager(this)

    fun getContentTypeByUrl(url: String): String? {
        if (sessionManager.checkSessionSync()) {
            requestGenerator.getContentTypeByUrl(url)
        }
        return null
    }

    fun getStoryById(id: String, getStoryCallback: GetStoryCallback) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.getStoryById(id, getStoryCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun changeUserId(userId: String) {
        this.userId = userId
        closeSession(null, object : EmptyCallback() {
            override fun onSuccess(response: Any?) {
                TODO("Not yet implemented")
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
        checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                TODO("Not yet implemented")
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getStoriesList(
        tags: String?,
        getStoriesListCallback: GetStoriesListCallback
    ) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.getStoriesList(tags, getStoriesListCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }

    fun getStoriesFavList(
        getStoriesListCallback: GetStoriesListCallback
    ) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.getStoriesFavList(getStoriesListCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }

    fun getStoriesFavImages(
        getStoriesListCallback: GetStoriesListCallback
    ) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.getStoriesFavImages(getStoriesListCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }

    fun getOnboardingStories(
        tags: String?,
        getStoriesListCallback: GetStoriesListCallback
    ) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.getOnboardingStories(tags, getStoriesListCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }


    fun sendProfilingTiming(task: ProfilingTask, callback: EmptyCallback? = null) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.sendProfilingTiming(task, callback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }


    fun sendStatV2(eventName: String, callback: EmptyCallback? = null, task: StatisticTaskV2) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.sendStatV2(eventName, callback, task)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })

    }

    fun sendStoryData(id: String, data: String) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {

                requestGenerator.sendStoryData(id, data)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun storyLikeDislike(id: String, value: String, callback: EmptyCallback? = null) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {

                requestGenerator.storyLikeDislike(id, value, callback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun storyFavorite(id: String, value: String, callback: EmptyCallback? = null) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.storyFavorite(id, value, callback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun share(id: String, shareCallback: StoryShareCallback) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {

                requestGenerator.share(id, shareCallback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun checkSession(openCallback: SessionOpenCallback) {
        sessionManager.checkSession(openCallback)
    }

    fun sendSessionStatistic(data: StatisticSessionObject, callback: EmptyCallback? = null) {
        sessionManager.checkSession(object : SessionOpenCallback() {
            override fun onSuccess(response: Any?) {
                requestGenerator.sendSessionStatistic(data, callback)
            }

            override fun onFailure(status: Int, error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun closeSession(data: StatisticSessionObject?, callback: EmptyCallback? = null) {
        sessionManager.closeSession(data, callback)
    }
}