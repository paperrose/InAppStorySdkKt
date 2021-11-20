package com.inappstory.sdk.ui.implementations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.R
import com.inappstory.sdk.ui.custominterfaces.IStoriesListItem
import com.inappstory.sdk.ui.views.VideoPlayer

class DefaultStoryListItem : IStoriesListItem {
    override fun getView(context: Context): View {
        return LayoutInflater.from(context)
            .inflate(
                R.layout.cs_story_default_list_image_item,
                null, false
            )
    }

    override fun getVideoView(context: Context): View {
        return LayoutInflater.from(context)
            .inflate(
                R.layout.cs_story_default_list_video_item,
                null, false
            )
    }

    override fun setTitle(itemView: View, title: String?, titleColor: Int?) {
        val titleV = itemView.findViewById<TextView>(R.id.title)
        titleV.text = title
        titleColor?.let {
            titleV.setTextColor(it)
        }

    }

    override fun setImage(itemView: View, url: String?, backgroundColor: Int) {
        val imageV = itemView.findViewById<ImageView>(R.id.image)
        url?.let {
            InAppStoryManager.imageLoader.displayImage(it, imageV, InAppStoryManager.cacheManager!!, true,
                backgroundColor = backgroundColor)
            return
        }
        imageV.setBackgroundColor(backgroundColor)
    }

    override fun setHasAudio(itemView: View, hasAudio: Boolean) {

    }

    override fun setVideo(itemView: View, videoUrl: String?, url: String?, backgroundColor: Int) {
        val imageV = itemView.findViewById<ImageView>(R.id.image)
        if (url.isNullOrEmpty()) {
            imageV.setBackgroundColor(backgroundColor)
        } else {
            InAppStoryManager.imageLoader.displayImage(url, imageV, InAppStoryManager.cacheManager!!, true,
                backgroundColor = backgroundColor)
        }
        val videoPlayer = itemView.findViewById<VideoPlayer>(R.id.video)
        videoUrl?.let {
            videoPlayer.release()
            videoPlayer.loadVideo(it)
        }
    }

    override fun setOpened(itemView: View, isOpened: Boolean) {

    }
}