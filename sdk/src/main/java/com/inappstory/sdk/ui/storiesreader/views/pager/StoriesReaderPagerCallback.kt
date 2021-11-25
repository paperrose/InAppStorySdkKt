package com.inappstory.sdk.ui.storiesreader.views.pager

interface StoriesReaderPagerCallback {
    fun swipeUp(currentItem: Int?)
    fun swipeDown(currentItem: Int?)
    fun swipeLeft(currentItem: Int)
    fun swipeRight(currentItem: Int)
    fun onPageScrolled(currentItem: Int, offset: Float, offsetPixels: Int)
    fun onPageSelected(currentItem: Int)
    fun onPageScrollStateChanged(state: Int)
}