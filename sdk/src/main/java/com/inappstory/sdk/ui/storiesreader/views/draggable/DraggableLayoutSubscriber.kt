package com.inappstory.sdk.ui.storiesreader.views.draggable

interface DraggableLayoutSubscriber {
    fun onDrag(
        elasticOffset: Float, elasticOffsetPixels: Float,
        rawOffset: Float, rawOffsetPixels: Float
    )
    fun onDragDismissed()
    fun onDragDropped()
    fun touchPause()
    fun touchResume()
    fun swipeDown(currentItem: Int? = null)
    fun swipeUp(currentItem: Int? = null)
}