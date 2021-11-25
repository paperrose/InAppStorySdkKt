package com.inappstory.sdk.ui.storiesreader.views.draggable

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.inappstory.sdk.R
import com.inappstory.sdk.ui.utils.AnimInterpolators
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min

class DraggableLayout(context: Context, attrs: AttributeSet?, defStyle: Int = 0) :
    FrameLayout(context, attrs, defStyle) {

    private var dragDismissDistance = Float.MAX_VALUE
    private var dragDismissScale = 1f
    private var dragElasticity = 0.8f
    private var shouldScale = false

    private var totalDrag = 0f
    private var totalDisabledDrag = 0f
    private var draggingDown = false
    private var draggingUp = false
    private var mLastActionEvent = 0

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }


    val viewModel: DraggableLayoutViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[DraggableLayoutViewModel::class.java]
        }
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        if (draggingDown && dy < 0 || draggingUp && dy > 0) {
            if (draggingUp || !viewModel!!.isDraggable())
                disabledDragScale(dy)
            else {
                dragScale(dy)
                consumed!![1] = dy
            }
        }
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScroll(
        target: View?,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        if (draggingUp || !viewModel!!.isDraggable())
            disabledDragScale(dyUnconsumed)
        else {
            dragScale(dyUnconsumed)
        }
    }

    override fun onStopNestedScroll(child: View?) {
        viewModel?.let {
            if (totalDisabledDrag > 400) {
                it.swipeUp()
            } else if (it.isDraggable() && totalDisabledDrag < -400) {
                it.swipeDown()
            }
            if (abs(totalDrag) >= dragDismissDistance &&
                it.isDraggable()
            ) {
                it.onDragDismissed()
            } else {
                if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                } else {
                    if (mLastActionEvent == MotionEvent.ACTION_MOVE) {
                        it.touchResume()
                    }
                    animate()
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200L)
                        .setInterpolator(AnimInterpolators.getInterpolator(context))
                        .setListener(null)
                        .start()
                }
                totalDrag = 0f
                totalDisabledDrag = 0f
                draggingUp = false
                draggingDown = draggingUp
                it.onDrag(0f, 0f, 0f, 0f)
                it.onDragDropped()
            }
        }


    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        mLastActionEvent = ev!!.action
        if (mLastActionEvent == MotionEvent.ACTION_MOVE) {
            viewModel?.touchPause()
        } else if (mLastActionEvent == MotionEvent.ACTION_UP
            || mLastActionEvent == MotionEvent.ACTION_CANCEL
        ) {
            viewModel?.touchResume()
        }
        return super.onInterceptTouchEvent(ev)
    }


    private fun disabledDragScale(scroll: Int) {
        if (scroll == 0) return
        totalDisabledDrag += scroll.toFloat()
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return
        totalDrag += scroll.toFloat()
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
        }
        var dragFraction = log10((1 + abs(totalDrag) / dragDismissDistance).toDouble())
            .toFloat()
        var dragTo: Float = dragFraction * dragDismissDistance * dragElasticity
        if (!draggingUp) {
            translationY = dragTo
            if (shouldScale) {
                val scale = 1 - (1 - dragDismissScale) * dragFraction
                scaleX = scale
                scaleY = scale
            }
        }
        if (draggingDown && totalDrag >= 0
            || draggingUp
        ) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDisabledDrag = dragTo
            totalDrag = totalDisabledDrag
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
        viewModel?.onDrag(
            dragFraction, dragTo,
            min(1f, abs(totalDrag) / dragDismissDistance), totalDrag
        )
    }

    init {
        with(
            getContext().obtainStyledAttributes(
                attrs, R.styleable.DraggableLayout, 0, 0
            )
        ) {
            if (hasValue(R.styleable.DraggableLayout_dragDismissDistance)) {
                dragDismissDistance = getDimensionPixelSize(
                    R.styleable.DraggableLayout_dragDismissDistance,
                    0
                ).toFloat()
            }
            if (hasValue(R.styleable.DraggableLayout_dragDismissScale)) {
                dragDismissScale = getFloat(
                    R.styleable.DraggableLayout_dragDismissScale,
                    dragDismissScale
                )
                shouldScale = dragDismissScale != 1f
            }
            recycle()
        }
    }
}