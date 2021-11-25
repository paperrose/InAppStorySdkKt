package com.inappstory.sdk.ui.storiesreader.views.webview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.inappstory.sdk.ui.storiesreader.views.pager.StoriesReaderPagerViewModel

class StoriesWebView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    WebView(context, attrs, defStyleAttr) {
    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

    private val readerViewModel: StoriesReaderPagerViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[StoriesReaderPagerViewModel::class.java]
        }
    }

    var lastTap: Long = 0

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> clickCoordinate = motionEvent.x
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    var clickCoordinate = 0f


    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        val c = super.onTouchEvent(motionEvent)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true
            }
        }
        return c
    }

    var touchSlider = false

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        val c = super.onInterceptTouchEvent(motionEvent)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false
            }

            //TODO pause slide

            lastTap = System.currentTimeMillis()
        } else if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
            touchSlider = false
            parentForAccessibility.requestDisallowInterceptTouchEvent(false)
        }
        return c || touchSlider
    }
}