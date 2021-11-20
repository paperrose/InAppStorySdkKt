package com.inappstory.sdk.ui.list.listitems

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.ui.implementations.DefaultFavoriteListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.Sizes
import kotlin.math.min

class FavoriteListItem(itemView: View) :
    StoriesListItem(
        itemView = itemView,
        isFavorite = true
    ) {

    override fun getDefaultCell(): View? {
        return customFavItem!!.getFavoriteItem(itemView.context)
    }

    override fun bind(
        storyData: CellStoryModelProtocol?,
        favoriteImages: List<FavoriteImage>?
    ) {

        customFavItem?.let {
            if (it is DefaultFavoriteListItem) {
                val textV = itemView.findViewById<TextView>(R.id.title)
                textV.text = "Favorites"
                csListItemTitleSize?.let { size ->
                    textV.textSize = size.toFloat()
                }
                customFont?.let { font ->
                    textV.typeface = font
                }
                textV.setTextColor(Color.BLACK)
                csListItemTitleColor?.let { color ->
                    textV.setTextColor(color)
                }
                it.itemWidth = Sizes.dpToPxExt(110, itemView.context)
                it.itemHeight = Sizes.dpToPxExt(110, itemView.context)
                csListItemWidth?.let { width ->
                    it.itemWidth = width - Sizes.dpToPxExt(10, itemView.context)
                }
                csListItemHeight?.let { height ->
                    it.itemHeight = height - Sizes.dpToPxExt(10, itemView.context)
                }
            }
            it.bindFavoriteItem(
                itemView,
                favoriteImages,
                min(favoriteImages!!.size, 4)
            )
        }
    }
}