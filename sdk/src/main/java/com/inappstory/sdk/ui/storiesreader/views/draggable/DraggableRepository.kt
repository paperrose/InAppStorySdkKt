package com.inappstory.sdk.ui.storiesreader.views.draggable

import androidx.lifecycle.MutableLiveData

object DraggableRepository {
    private var csIsDraggable: Boolean = true
    private val storyIsDraggable: MutableLiveData<Boolean> = MutableLiveData(true)

    fun csIsDraggable(isDraggable: Boolean) {
        csIsDraggable = isDraggable
    }

    fun screenIsDraggable() : Boolean {
        return storyIsDraggable.value!!
    }

    fun setScreenDraggable(isDraggable: Boolean) {
        storyIsDraggable.postValue(csIsDraggable && isDraggable)
    }
}