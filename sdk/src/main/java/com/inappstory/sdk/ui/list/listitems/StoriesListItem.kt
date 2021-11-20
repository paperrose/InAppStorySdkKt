package com.inappstory.sdk.ui.list.listitems

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.ImageQuality
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.ui.custominterfaces.IGetFavoriteListItem
import com.inappstory.sdk.ui.custominterfaces.IStoriesListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.Sizes
import kotlin.math.max

open class StoriesListItem(
    itemView: View,
    var isFavorite: Boolean = false,
    var hasVideo: Boolean = false
) : RecyclerView.ViewHolder(itemView) {
    var customFont: Typeface? = null
    var csListItemHeight: Int? = null
    var csListItemWidth: Int? = null
    var csListItemBorderColor: Int? = null
    var csListItemTitleColor: Int? = null
    var csListItemTitleSize: Int? = null
    var csListItemMargin: Int? = null
    var customItem: IStoriesListItem? = null
    var customFavItem: IGetFavoriteListItem? = null
    var quality: ImageQuality? = ImageQuality.MEDIUM
    var data: CellStoryModelProtocol? = null

    fun create(
        customFont: Typeface? = null,
        csListItemHeight: Int? = null,
        csListItemWidth: Int? = null,
        csListItemBorderColor: Int? = null,
        csListItemTitleColor: Int? = null,
        csListItemTitleSize: Int? = null,
        csListItemMargin: Int? = null,
        customItem: IStoriesListItem? = null,
        customFavItem: IGetFavoriteListItem? = null,
        data: CellStoryModelProtocol? = null,
        quality: ImageQuality? = null
    ) {
        this.customFont = customFont
        this.data = data
        this.csListItemBorderColor = csListItemBorderColor
        this.csListItemHeight = csListItemHeight
        this.csListItemWidth = csListItemWidth
        this.csListItemTitleColor = csListItemTitleColor
        this.csListItemTitleSize = csListItemTitleSize
        this.csListItemMargin = csListItemMargin
        this.customItem = customItem
        this.customFavItem = customFavItem
        quality?.let {
            this.quality = it
        }
        val baseLayout = itemView.findViewById<ViewGroup>(R.id.baseLayout)
        baseLayout?.removeAllViews()
        getDefaultCell()?.let {
            baseLayout?.addView(it)
        }
        csListItemMargin?.let {
            val margin = max(0, it)
            val lp = itemView.layoutParams as RecyclerView.LayoutParams
            lp.setMargins(
                Sizes.dpToPxExt(margin / 2), 0,
                Sizes.dpToPxExt(margin / 2), 0
            )
            itemView.layoutParams = lp
        }
    }

    open fun bind(
        storyData: CellStoryModelProtocol? = null,
        favoriteImages: List<FavoriteImage>? = null
    ) {

    }

    open fun getDefaultCell(): View? {
        return null
    }
}