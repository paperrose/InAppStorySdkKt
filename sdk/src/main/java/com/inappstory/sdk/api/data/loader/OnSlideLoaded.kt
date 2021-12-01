package com.inappstory.sdk.api.data.loader

interface OnSlideLoaded {
    fun onSlideLoaded(storyId: String, index: Int)
    fun onSlideError(storyId: String, index: Int)
}