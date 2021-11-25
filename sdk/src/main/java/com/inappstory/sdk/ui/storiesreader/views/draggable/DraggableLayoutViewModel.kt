package com.inappstory.sdk.ui.storiesreader.views.draggable

import androidx.lifecycle.ViewModel

class DraggableLayoutViewModel : ViewModel() {
    val subscribers: HashSet<DraggableLayoutSubscriber> = HashSet()

    fun isDraggable(): Boolean {
        return DraggableRepository.screenIsDraggable()
    }

    fun setDraggable(isDraggable: Boolean) {
        DraggableRepository.csIsDraggable(isDraggable)
    }

    fun onDrag(
        elasticOffset: Float, elasticOffsetPixels: Float,
        rawOffset: Float, rawOffsetPixels: Float
    ) {
        subscribers.forEach {
            it.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels)
        }
    }

    fun onDragDismissed() {
        subscribers.forEach {
            it.onDragDismissed()
        }
    }

    fun onDragDropped() {
        subscribers.forEach {
            it.onDragDismissed()
        }
    }

    fun touchPause() {
        subscribers.forEach {
            it.touchPause()
        }
    }

    fun touchResume() {
        subscribers.forEach {
            it.touchResume()
        }
    }

    fun swipeDown() {
        subscribers.forEach {
            it.swipeDown()
        }
    }

    fun swipeUp() {
        subscribers.forEach {
            it.swipeUp()
        }
    }
}