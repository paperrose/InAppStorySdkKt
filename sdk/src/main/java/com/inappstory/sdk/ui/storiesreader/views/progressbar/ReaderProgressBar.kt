package com.inappstory.sdk.ui.storiesreader.views.progressbar

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.inappstory.sdk.R
import com.inappstory.sdk.utils.common.Sizes

class ReaderProgressBar(context: Context, attrs: AttributeSet?, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

    private val frameDuration = 50 // ms


    private val strokeWidth = Sizes.dpToPxExt(4, context).toFloat()
    private val strokeHalfSize = strokeWidth / 2


    fun getProgress(): Int {
        return progress
    }

    private var progress = 0

    private var color = 0

    private var currentFrame = 0

    private var lastFrameShown: Long = 0

    private var arcRect: RectF? = null


    private var tf: Typeface? = null

    init {
        tf = Typeface.DEFAULT
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun setProgress(progress: Int) {
        if (progress < 0 || progress > 100) {
            throw IndexOutOfBoundsException("progress must be 0..100")
        }
        this.progress = progress
    }

    private var colorPaint: Paint? = null

    private fun getColorPaint(resources: Resources): Paint {
        if (colorPaint == null) {
            colorPaint = Paint()
            colorPaint!!.color = resources.getColor(R.color.cs_loaderColor)
            colorPaint!!.style = Paint.Style.STROKE
            colorPaint!!.strokeWidth = strokeWidth
            colorPaint!!.isAntiAlias = true
        }
        return colorPaint!!
    }

    override fun onDraw(canvas: Canvas) {
        if (arcRect == null) {
            arcRect = RectF(
                strokeHalfSize, strokeHalfSize,
                canvas.width - strokeHalfSize,
                canvas.height - strokeHalfSize
            )
        }
        val paint = getColorPaint(resources)
        paint.isAntiAlias = true
        val now = SystemClock.uptimeMillis()
        if (now > lastFrameShown + frameDuration) {
            currentFrame++
            lastFrameShown = now
        }
        paint.color = context.resources.getColor(R.color.cs_loaderColor)
        paint.typeface = tf
        paint.textSize = Sizes.dpToPxExt(12, context).toFloat()
        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        drawProgress(canvas, paint)
        invalidate()
    }

    private fun drawProgress(canvas: Canvas, paint: Paint) {
        canvas.save()
        canvas.rotate(-90f, (canvas.width / 2).toFloat(), (canvas.height / 2).toFloat())
        val deg = (3.6f * progress).toInt()
        canvas.drawArc(arcRect!!, 0f, deg.toFloat(), false, paint)
        canvas.restore()
    }
}