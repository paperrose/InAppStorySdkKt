package com.inappstory.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.inappstory.sdk.utils.common.Sizes

class RoundedCornerLayout(context: Context, attrs: AttributeSet?, defStyle: Int = 0) :
    FrameLayout(context, attrs, defStyle) {

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }


    private var path: Path? = null
    var radius = 0

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val cornerRadius = radius.toFloat()
        path = Path()
        path?.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )
    }


    override fun dispatchDraw(canvas: Canvas?) {
        path?.let {
            canvas?.clipPath(it)
        }
        super.dispatchDraw(canvas)
    }


    init {
        radius = Sizes.dpToPxExt(16, context)
    }
}