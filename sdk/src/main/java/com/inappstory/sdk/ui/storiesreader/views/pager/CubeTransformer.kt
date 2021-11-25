package com.inappstory.sdk.ui.storiesreader.views.pager

import android.view.View
import androidx.viewpager.widget.ViewPager

class CubeTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        onTransform(page, position)
    }

    private fun onTransform(view: View, position: Float) {
        if (position < 0f)
            view.pivotX = view.width.toFloat()
        else
            view.pivotX = 0f
        view.pivotY = view.height * 0.6f
        view.rotationY = 60 * position
    }

}