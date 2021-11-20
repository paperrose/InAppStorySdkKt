package com.inappstory.sdk.utils.common

import android.graphics.Color
import java.lang.NumberFormatException

object Colors {
    fun parseColor(color: String?, defaultColor: Int?): Int? {
        color?.let {
            return try {
                Color.parseColor(it)
            } catch (ex: NumberFormatException) {
                defaultColor
            }
        }
        return defaultColor
    }
}