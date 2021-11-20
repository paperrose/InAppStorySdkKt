package com.inappstory.sdk.ui.list.listitems

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.ui.implementations.DefaultStoryListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.Colors

class VideoStoryListItem(itemView: View) :
    StoriesListItem(itemView = itemView,
    hasVideo = true)  {

    override fun getDefaultCell(): View {
        return customItem!!.getVideoView(itemView.context)
    }

    override fun bind(storyData: CellStoryModelProtocol?, favoriteImages: List<FavoriteImage>?) {
        super.bind(storyData, favoriteImages)
        customItem?.let {
            if (it is DefaultStoryListItem) {
                val textV = itemView.findViewById<TextView>(R.id.title)
                val container = itemView.findViewById<View>(R.id.container)
                csListItemTitleSize?.let { size ->
                    textV.textSize = size.toFloat()
                }
                customFont?.let { font ->
                    textV.typeface = font
                }
                csListItemWidth?.let { width->
                    container.layoutParams.width = width
                }
                csListItemHeight?.let { height->
                    container.layoutParams.height = height
                }
            }
            it.setTitle(itemView, storyData!!.title(),
                Colors.parseColor(storyData.titleColor(), null))
            it.setHasAudio(itemView, storyData.hasAudio())
            val cover = storyData.imageCover(quality)!!.url
            val color = Colors.parseColor(storyData.backgroundColor(), Color.BLACK)!!
            it.setImage(itemView, cover, color)
            it.setOpened(itemView, storyData.isOpened())
            storyData.videoCoverUrl()?.let { url->
                it.setVideo(itemView, url, cover, color)
            }
        }
    }

}