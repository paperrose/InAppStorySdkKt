package com.inappstory.sdk.ui.storiesreader.views.pager

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class DepthTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        onTransform(page, position)
    }

    private fun onTransform(view: View, position: Float) {
        val minScale = 0.85f
        when {
            position <= -1.0 -> {
                view.translationX = 0f
            }
            position <= 0f -> {
                val scaleFactor = minScale + (1 - minScale) * (1 - abs(position))
                view.alpha = 1 + position
                view.pivotY = 0.5f * view.height
                view.translationX = view.width * -position
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            }
            position <= 1f -> {
                view.translationX = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            position > 1 -> {
                view.translationX = view.width.toFloat()
            }
        }
    }

}