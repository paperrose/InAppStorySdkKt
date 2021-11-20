package com.inappstory.sdk.ui.list.listitems

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.ui.implementations.DefaultStoryListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.Colors

class ImageStoryListItem(itemView: View) :
    StoriesListItem(itemView = itemView) {

    override fun getDefaultCell(): View {
        return customItem!!.getView(itemView.context)
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
            it.setTitle(
                itemView, storyData!!.title(),
                Colors.parseColor(storyData.titleColor(), csListItemTitleColor)
            )
            it.setHasAudio(itemView, storyData.hasAudio())
            it.setImage(
                itemView, storyData.imageCover(quality)!!.url,
                Colors.parseColor(storyData.backgroundColor(), Color.BLACK)!!
            )
            it.setOpened(itemView, storyData.isOpened())
        }
    }

}