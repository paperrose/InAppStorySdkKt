package com.inappstory.sdk.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.R
import kotlin.math.roundToInt

object Sizes {
    fun getStatusBarHeight(context: Context?): Int {
        if (context == null) return 60
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        result = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            Math.ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * context.resources.displayMetrics.density).toDouble())
                .toInt()
        }
        return result
    }


    fun getPixelScaleFactorExt(): Float {
        val con: Context = InAppStoryManager.context ?: return 1f
        val displayMetrics = con.resources.displayMetrics
        return displayMetrics.density
    }

    fun getPixelScaleFactorExt(context: Context?): Float {
        val con = context ?: return 1f
        val displayMetrics = con.resources.displayMetrics
        return displayMetrics.density
    }

    fun getScreenSize(): Point {
        var con: Context = InAppStoryManager.context ?: return Point(0, 0)
        val wm = con.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun getScreenSize(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun dpToPxExt(dp: Int): Int {
        return (dp * getPixelScaleFactorExt()).roundToInt()
    }

    fun isTablet(): Boolean {
        return if (InAppStoryManager
                .context != null
        ) InAppStoryManager
            .context!!.resources
            .getBoolean(R.bool.isTablet) else false
    }

    fun dpToPxExt(dp: Int, context: Context?): Int {
        return (dp * getPixelScaleFactorExt(context)).roundToInt()
    }

    fun pxToDpExt(dp: Int): Int {
        return (dp / getPixelScaleFactorExt()).roundToInt()
    }

    fun pxToDpExt(dp: Int, context: Context?): Int {
        return (dp / getPixelScaleFactorExt(context)).roundToInt()
    }
}