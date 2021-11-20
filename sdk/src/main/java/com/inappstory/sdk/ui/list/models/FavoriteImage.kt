package com.inappstory.sdk.ui.list.models

import android.graphics.Color
import com.inappstory.sdk.api.data.models.Image
import com.inappstory.sdk.utils.common.Colors

class FavoriteImage(
    var id: String,
    var image: List<Image>? = null,
    var backgroundColor: String? = null
) {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return id == (other as FavoriteImage).id
        }
        return super.equals(other)
    }

    fun getBackgroundColor(): Int {
        return Colors.parseColor(backgroundColor, Color.BLACK)!!
    }

    fun getUrl(): String {
        if (image.isNullOrEmpty()) return ""
        return image!![0].url
    }
}