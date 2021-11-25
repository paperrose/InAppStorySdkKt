package com.inappstory.sdk.ui.storiesreader.views.pager

import androidx.lifecycle.MutableLiveData

object StoriesReaderPagerRepository {
    private val blocked: MutableLiveData<Boolean> = MutableLiveData(false)

    fun viewIsBlocked() : Boolean {
        return blocked.value!!
    }

    fun viewIsBlocked(isBlocked: Boolean) {
        blocked.postValue(isBlocked)
    }
}