package com.inappstory.sdk.ui.storiesreader.views.webview

import android.content.Context
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.dispatchers.Dispatcher
import com.inappstory.sdk.ui.storiesreader.page.ReaderPageManager
import com.inappstory.sdk.ui.storiesreader.views.progressbar.ReaderProgressBar
import com.inappstory.sdk.utils.json.JsonParser
import com.inappstory.sdk.utils.json.SerializedName
import java.io.File

class StoriesWebViewPageManager(context: Context) : ReaderPageManager, Dispatcher() {

    class StoryLoadedData(
        id: String,
        index: Int?
    ) {
        @SerializedName("id")
        var id: String? = id

        @SerializedName("index")
        var index: Int? = index

        constructor() : this("", null)
    }


    val context = context
    var id: String? = null
    var slideIndex: Int = 0

    override fun getProgressBar(): ReaderProgressBar? {
        return null
    }

    override fun getStoryId(): String? {
        return id
    }

    override fun getIndex(): Int {
        return slideIndex
    }

    override fun showSingleStory(id: Int, slideIndex: Int) {
        success(
            arrayOf(
                StoriesWebViewMessages.SHOW_SINGLE,
                id.toString(),
                slideIndex
            )
        )
    }

    override fun sendApiRequest(data: String?) {
    }

    override fun openGameReader(
        gameFile: String,
        coverFile: String?,
        initCode: String?,
        gameResources: String?
    ) {
    }

    override fun setAudioManagerMode(mode: String) {
        success(
            arrayOf(
                StoriesWebViewMessages.SET_AUDIO_MODE,
                mode
            )
        )
    }

    override fun storyShowNext() {
        success(
            arrayOf(
                StoriesWebViewMessages.NEXT_STORY
            )
        )
    }

    override fun storyShowPrev() {
        success(
            arrayOf(
                StoriesWebViewMessages.PREV_STORY
            )
        )
    }

    override fun storyClick(payload: String) {
        var data: String? = payload
        val forbidden = payload == "forbidden"
        if (payload.isNullOrEmpty() || payload == "test" || forbidden)
            data = null
        success(
            arrayOf(
                StoriesWebViewMessages.SLIDE_CLICK,
                data,
                forbidden
            )
        )
    }

    override fun resetTimers() {
    }

    override fun restartStoryWithDuration(delay: Long) {
    }

    override fun storyShowTextInput(id: String?, data: String?) {
    }

    override fun storyStartedEvent() {
        success(
            arrayOf(
                StoriesWebViewMessages.SLIDE_STARTED
            )
        )
        //pageFinished()
        //timers and profiling
    }

    override fun storyLoaded(data: String?) {
        if (data != null) {
            val slideIndex = (JsonParser().jsonToPOJO(
                data, StoryLoadedData::class,
                false
            ) as StoryLoadedData).index
            success(
                arrayOf(
                    StoriesWebViewMessages.SLIDE_LOADED,
                    slideIndex
                )
            )
        } else {
            success(
                arrayOf(
                    StoriesWebViewMessages.SLIDE_LOADED,
                    -1
                )
            )
        }
    }

    override fun storyLoaded(slideIndex: Int) {
        success(
            arrayOf(
                StoriesWebViewMessages.SLIDE_LOADED,
                slideIndex
            )
        )
    }

    override fun storyStatisticEvent(name: String, data: String) {
    }

    override fun share(id: String, data: String) {
    }

    override fun storyFreezeUI() {
        success(
            arrayOf(
                StoriesWebViewMessages.FREEZE
            )
        )
    }

    override fun storySendData(data: String) {

    }

    override fun storySetLocalData(data: String, sendToServer: Boolean) {

    }

    override fun storyGetLocalData(): String? {
        return null
    }

    override fun storyResumedEvent(startTime: Double) {
    }

    override fun changeIndex(slideIndex: Int) {
        success(
            arrayOf(
                StoriesWebViewMessages.CHANGE_INDEX,
                slideIndex
            )
        )
    }

    override fun getCurrentFile(img: String): File? {
        return InAppStoryManager.cacheManager?.commonCache?.get(img)
    }
}