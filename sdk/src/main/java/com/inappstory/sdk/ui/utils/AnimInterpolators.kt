package com.inappstory.sdk.ui.utils

import android.R
import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator

object AnimInterpolators {
    var fastOutSlowIn: Interpolator? = null

    fun getInterpolator(context: Context?): Interpolator? {
        if (fastOutSlowIn == null) {
            fastOutSlowIn =
                AnimationUtils.loadInterpolator(
                    context,
                    R.interpolator.fast_out_slow_in
                )
        }
        return fastOutSlowIn
    }
}