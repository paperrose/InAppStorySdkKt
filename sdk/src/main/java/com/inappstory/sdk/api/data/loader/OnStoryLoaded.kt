package com.inappstory.sdk.api.data.loader

import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol

interface OnStoryLoaded {
    fun onStoryLoaded(story: ReaderStoryDataProtocol)
    fun onStoryError()
}