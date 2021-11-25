package com.inappstory.sdk.ui.utils

import android.app.Activity

object ScreensManager {
    fun openGameReader() {

    }

    fun openStoriesReader(baseScreen: Activity, storiesIds: Array<String>?, openStoryId: String? = null, openSlideIndex: Int = 0, source: Int) {
        var index = 0
        if (storiesIds.isNullOrEmpty()) return
        openStoryId?.let {
            index = storiesIds.indexOf(it)
        }

    }
}