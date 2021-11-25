package com.inappstory.sdk.ui.storiesreader.views.pager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlin.math.sqrt

class StoriesReaderPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs),
    OnPageChangeListener {
    constructor(context: Context)
            : this(context, null) {
    }

    var callback: StoriesReaderPagerCallback? = null

    private val viewModel: StoriesReaderPagerViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[StoriesReaderPagerViewModel::class.java]
        }
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val res = super.getChildDrawingOrder(childCount, i)
        return childCount - res - 1
    }

    private var pressedX = 0f
    private var pressedY = 0f

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        viewModel?.let {
            if (it.isBlocked()) return true
        }
        var pressedEndX = 0f
        var pressedEndY = 0f
        var distance = false
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            pressedX = motionEvent.x
            pressedY = motionEvent.y
        } else if (!(motionEvent.action == MotionEvent.ACTION_UP
                    || motionEvent.action == MotionEvent.ACTION_CANCEL)
        ) {
            pressedEndX = motionEvent.x - pressedX
            pressedEndY = motionEvent.y - pressedY
            distance = sqrt(pressedEndX * pressedEndX + pressedEndY * pressedEndY) > 20f
        } else if (motionEvent.action == MotionEvent.ACTION_UP
            || motionEvent.action == MotionEvent.ACTION_CANCEL
        ) {
            pressedEndY = motionEvent.y - pressedY
            pressedEndX = motionEvent.x - pressedX
            if (pressedEndY > 400) {
                callback?.swipeDown(currentItem)
                return true
            }
            if (pressedEndY < -400) {
                callback?.swipeUp(currentItem)
                return true
            }
            if (currentItem == 0 && pressedEndX * pressedEndX > pressedEndY * pressedEndY && pressedEndX > 300) {
                callback?.swipeRight(currentItem)
                return true
            }
            if (currentItem == adapter!!.count - 1 && pressedEndX * pressedEndX > pressedEndY * pressedEndY && pressedEndX < -300) {
                callback?.swipeLeft(currentItem)
                return true
            }
        }
        val distanceX = pressedEndX * pressedEndX;
        val distanceY = pressedEndX * pressedEndX;
        if (motionEvent.action == MotionEvent.ACTION_MOVE) {
            return !(distanceX < distanceY
                    && sqrt(distanceY) > 20
                    ) && (distance
                    && !(currentItem == 0
                    && distanceX > distanceY
                    && pressedEndX > 0)
                    && !(currentItem == adapter!!.count - 1
                    && distanceX > distanceY
                    && pressedEndX < 0))
        }
        return super.onInterceptTouchEvent(motionEvent)
    }

    override fun onPageSelected(position: Int) {
        callback?.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        callback?.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        super.onPageScrolled(position, offset, offsetPixels)
        if (offset == 0f) {
            viewModel?.viewIsBlocked(false)
            requestDisallowInterceptTouchEvent(false)
        } else {
            viewModel?.viewIsBlocked(true)
            requestDisallowInterceptTouchEvent(true)
        }
        callback?.onPageScrolled(position, offset, offsetPixels)
    }
}