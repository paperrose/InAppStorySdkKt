package com.inappstory.sdk.ui.storiesreader.page

import com.inappstory.sdk.ui.storiesreader.views.progressbar.ReaderProgressBar
import java.io.File

interface ReaderPageManager {
    fun getProgressBar(): ReaderProgressBar?
    fun getStoryId(): String?
    fun getIndex(): Int
    fun showSingleStory(id: Int, slideIndex: Int)
    fun sendApiRequest(data: String?)
    fun openGameReader(gameFile: String,
                       coverFile: String?,
                       initCode: String?,
                       gameResources: String?)
    fun setAudioManagerMode(mode: String)
    fun storyShowNext()
    fun storyShowPrev()
    fun storyClick(payload: String)
    fun resetTimers()
    fun restartStoryWithDuration(delay: Long)
    fun storyShowTextInput(id: String?, data: String?)
    fun storyStartedEvent()
    fun storyLoaded(data: String?)
    fun storyStatisticEvent(name: String, data: String)
    fun share(id: String, data: String)
    fun storyFreezeUI()
    fun storySendData(data: String)
    fun storySetLocalData(data: String, sendToServer: Boolean)
    fun storyGetLocalData(): String?
    fun storyResumedEvent(startTime: Double)
    fun storyLoaded(slideIndex: Int)
    fun changeIndex(slideIndex: Int)
    fun getCurrentFile(img: String): File?
}