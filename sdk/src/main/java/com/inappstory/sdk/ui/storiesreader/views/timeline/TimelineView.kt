package com.inappstory.sdk.ui.storiesreader.views.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.inappstory.sdk.utils.common.Sizes
import kotlin.math.max
import kotlin.math.min

class TimelineView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    var storiesTimer = StoriesTimer()
    private val segments = mutableListOf<TimelineSegment>()
    private val segmentHeight = Sizes.dpToPxExt(3, context)
    private val marginWidth = Sizes.dpToPxExt(3, context)
    private val backgroundColor = Color.parseColor("#80FFFFFF")
    private val fillColor = Color.parseColor("#FFFFFF")
    val viewModel: TimelineViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            val res = ViewModelProvider(it)[TimelineViewModel::class.java]
            res
        }
    }

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }



    fun initSegments() {
        viewModel?.storiesTimer = storiesTimer
        segments.clear()
        val count = storiesTimer.getDurationsCount()
        for (i in 0 until count) {
            segments.add(TimelineSegment(i))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        segments.forEachIndexed { index, segment ->
            val drawingComponents = drawSegment(index, segment)
            drawingComponents.first.forEachIndexed { drawingIndex, rectangle ->
                canvas?.drawRect(
                    rectangle,
                    drawingComponents.second[drawingIndex]
                )
            }
        }
        invalidate()
    }


    fun setActive(isActive: Boolean) {
        viewModel?.isActive = isActive
        if (!isActive) {
            stopTimeline()
            viewModel?.stopTimer()
            viewModel?.clearTimer()
        }
    }


    fun setSlideCompleted(selected: Int) {
        if (segments.isEmpty()) return
        var ind = min(max(selected,0), segments.size-1)
        segments[ind].state = TimelineSegment.TimelineSegmentState.FILLED
    }

    fun stopTimeline() {
        segments.forEach {
            if (it.state == TimelineSegment.TimelineSegmentState.ANIMATED)
                it.state = TimelineSegment.TimelineSegmentState.EMPTY
        }
    }

    fun setCurrentSlide(selected: Int) {
        var ind = min(max(selected,0), segments.size)
        segments.mapIndexed { index, segment ->
            when {
                index < ind -> segment.state = TimelineSegment.TimelineSegmentState.FILLED
                index == ind -> segment.state = TimelineSegment.TimelineSegmentState.ANIMATED
                index > ind -> segment.state = TimelineSegment.TimelineSegmentState.EMPTY
            }
        }

    }

    private fun drawSegment(
        index: Int,
        segment: TimelineSegment
    ): Pair<MutableList<RectF>, MutableList<Paint>> {
        val rectangles = mutableListOf<RectF>()
        val paints = mutableListOf<Paint>()
        val segmentWidth: Float =
            (width - (segments.size - 1) * marginWidth).toFloat() / segments.size
        val startBound = index * segmentWidth + ((index) * marginWidth)
        var progressBound = startBound
        viewModel?.let {
            progressBound += segmentWidth * it.getProgress()
        }
        val endBound = startBound + segmentWidth
        val backgroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = backgroundColor
        }
        val foregroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = fillColor
        }
        val fillPaint =
            if (segment.state == TimelineSegment.TimelineSegmentState.FILLED) foregroundPaint else backgroundPaint
        rectangles.add(RectF(startBound, 0f, endBound, segmentHeight.toFloat()))
        paints.add(fillPaint)
        if (segment.state == TimelineSegment.TimelineSegmentState.ANIMATED) {
            rectangles.add(RectF(startBound, 0f, progressBound, segmentHeight.toFloat()))
            paints.add(foregroundPaint)
        }
        return Pair(rectangles, paints)
    }
}