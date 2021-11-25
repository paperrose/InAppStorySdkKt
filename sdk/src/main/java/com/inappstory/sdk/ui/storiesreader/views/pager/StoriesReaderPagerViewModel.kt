package com.inappstory.sdk.ui.storiesreader.views.pager

import androidx.lifecycle.ViewModel

class StoriesReaderPagerViewModel : ViewModel() {
    fun isBlocked(): Boolean {
        return StoriesReaderPagerRepository.viewIsBlocked()
    }

    fun viewIsBlocked(isBlocked: Boolean) {
        StoriesReaderPagerRepository.viewIsBlocked(isBlocked)
    }
}