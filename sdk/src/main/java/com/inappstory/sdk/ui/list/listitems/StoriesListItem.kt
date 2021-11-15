package com.inappstory.sdk.ui.list.listitems

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.inappstory.sdk.AppearanceManager

open class StoriesListItem(itemView: View,
                      var isFavorite: Boolean = false,
                      var hasVideo: Boolean = false) : RecyclerView.ViewHolder(itemView) {
    open fun bind() {

    }
}