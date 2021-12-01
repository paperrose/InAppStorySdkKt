package com.inappstory.sdk.ui.storiesreader.views.webview

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.inappstory.sdk.ui.storiesreader.page.ReaderPageManager

class StoriesJSInterface(manager: ReaderPageManager) {
    private var manager: ReaderPageManager = manager

    private fun getMethodName(): String {
        val ste = Thread.currentThread().stackTrace
        return ste[4].methodName
    }

    private fun logMethod(payload: String) {
        Log.e("JS_method_test", "${manager.getStoryId()} ${getMethodName()} $payload")
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    fun storyClick(payload: String) {
        manager.storyClick(payload)
        logMethod(payload)
    }


    @JavascriptInterface
    fun storyShowSlide(index: Int) {
        if (manager.getIndex() != index) {
            manager.changeIndex(index)
        }
        logMethod("" + index)
    }

    @JavascriptInterface
    fun showSingleStory(id: Int, index: Int) {
        logMethod("$id $index")
        if (manager.getStoryId() != id.toString()) {
            manager.showSingleStory(id, index)
        } else if (manager.getIndex() != index) {
            manager.changeIndex(index)
        }
    }

    @JavascriptInterface
    fun sendApiRequest(data: String?) {
        manager.sendApiRequest(data)
    }

    @JavascriptInterface
    fun openGameReader(
        gameFile: String,
        coverFile: String?,
        initCode: String?,
        gameResources: String?
    ) {
        manager.openGameReader(gameFile, coverFile, initCode, gameResources)
        logMethod(gameFile)
    }


    @JavascriptInterface
    fun setAudioManagerMode(mode: String) {
        manager.setAudioManagerMode(mode)
        logMethod(mode)
    }


    @JavascriptInterface
    fun storyShowNext() {
        manager.storyShowNext()
        logMethod("")
    }

    @JavascriptInterface
    fun storyShowPrev() {
        manager.storyShowPrev()
        logMethod("")
    }

    @JavascriptInterface
    fun resetTimers() {
        manager.resetTimers()
        logMethod("")
    }

    @JavascriptInterface
    fun storyShowNextSlide(delay: Long) {
        if (delay != 0L) {
            Log.e("jsDuration", "$delay showNext")
            manager.restartStoryWithDuration(delay)
        } else {
            manager.changeIndex(manager.getIndex() + 1)
        }
        logMethod("" + delay)
    }

    @JavascriptInterface
    fun storyShowTextInput(id: String?, data: String?) {
        manager.storyShowTextInput(id, data)
        logMethod("")
    }

    @JavascriptInterface
    fun storyStarted() {
        manager.storyStartedEvent()
        logMethod("")
    }

    @JavascriptInterface
    fun storyStarted(startTime: Double) {
        manager.storyStartedEvent()
        logMethod("" + startTime)
    }

    @JavascriptInterface
    fun storyResumed(startTime: Double) {
        manager.storyResumedEvent(startTime)
        logMethod("" + startTime)
    }

    @JavascriptInterface
    fun storyLoaded() {
        manager.storyLoaded(-1)
        logMethod("")
    }

    @JavascriptInterface
    fun storyLoaded(data: String?) {
        manager.storyLoaded(data)
        logMethod(data + "")
    }


    @JavascriptInterface
    fun storyStatisticEvent(name: String, data: String) {
        manager.storyStatisticEvent(name, data)
        logMethod("$name $data")
    }

    @JavascriptInterface
    fun emptyLoaded() {
        logMethod("")
    }

    @JavascriptInterface
    fun share(id: String, data: String) {
        manager.share(id, data)
        logMethod("$id $data")
    }

    @JavascriptInterface
    fun storyFreezeUI() {
        manager.storyFreezeUI()
        logMethod("")
    }


    @JavascriptInterface
    fun storySendData(data: String) {
        manager.storySendData(data)
        logMethod(data)
    }

    @JavascriptInterface
    fun storySetLocalData(data: String, sendToServer: Boolean) {
        synchronized(manager) {
            manager.storySetLocalData(data, sendToServer)
            logMethod("$data $sendToServer")
        }
    }


    @JavascriptInterface
    fun storyGetLocalData(): String {
        synchronized(manager) {
            val res = manager.storyGetLocalData()
            logMethod(res ?: "")
            return res ?: ""
        }
    }


    @JavascriptInterface
    fun defaultTap(data: String) {
        logMethod(data)
    }
}