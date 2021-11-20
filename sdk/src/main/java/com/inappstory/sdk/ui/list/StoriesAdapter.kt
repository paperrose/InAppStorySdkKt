package com.inappstory.sdk.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inappstory.sdk.AppearanceManager
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.ui.list.listitems.FavoriteListItem
import com.inappstory.sdk.ui.list.listitems.ImageStoryListItem
import com.inappstory.sdk.ui.list.listitems.StoriesListItem
import com.inappstory.sdk.ui.list.listitems.VideoStoryListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage

class StoriesAdapter(
    var appearanceManager: AppearanceManager,
    var stories: ArrayList<CellStoryModelProtocol>,
    var favoriteImages: ArrayList<FavoriteImage>? = ArrayList()
) :
    RecyclerView.Adapter<StoriesListItem>() {

    fun addFavorite(favoriteImage: FavoriteImage) {
        favoriteImages?.forEach {
            if (it.id == favoriteImage.id) return
        }
        favoriteImages?.let {
            if (it.isNotEmpty()) {
                it.add(favoriteImage)
                notifyItemChanged(itemCount - 1)
            } else {
                it.add(favoriteImage)
                notifyDataSetChanged()
            }
        }
    }

    fun removeFavorite(storyId: String) {
        var favoriteImage: FavoriteImage? = null
        favoriteImages?.forEach {
            if (it.id == storyId) favoriteImage = it
        }
        favoriteImage?.let {
            favoriteImages?.remove(it)
        }
        favoriteImages?.let {
            if (it.isNotEmpty()) {
                notifyItemChanged(itemCount - 1)
            } else {
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesListItem {
        val vType = viewType % 10
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.cs_story_list_item_container, parent, false)
        val res: StoriesListItem
        when {
            vType == 3 -> {
                res = FavoriteListItem(v)
                res.create(
                    csListItemWidth = appearanceManager.csListItemWidth,
                    csListItemHeight = appearanceManager.csListItemHeight,
                    csListItemBorderColor = appearanceManager.csListItemBorderColor,
                    csListItemTitleColor = appearanceManager.csListItemTitleColor,
                    csListItemMargin = appearanceManager.csListItemMargin,
                    customFont = appearanceManager.csCustomFont,
                    quality = appearanceManager.csCoverQuality,
                    customFavItem = appearanceManager.csFavoriteListItemInterface)
            }
            vType > 5 -> {
                res = VideoStoryListItem(v)
                res.create(
                    data = stories[viewType / 10],
                    csListItemWidth = appearanceManager.csListItemWidth,
                    csListItemHeight = appearanceManager.csListItemHeight,
                    csListItemBorderColor = appearanceManager.csListItemBorderColor,
                    csListItemTitleColor = appearanceManager.csListItemTitleColor,
                    csListItemMargin = appearanceManager.csListItemMargin,
                    customFont = appearanceManager.csCustomFont,
                    quality = appearanceManager.csCoverQuality,
                    customItem = appearanceManager.csListItemInterface
                )
            }
            else -> {
                res = ImageStoryListItem(v)
                res.create(
                    data = stories[viewType / 10],
                    csListItemWidth = appearanceManager.csListItemWidth,
                    csListItemHeight = appearanceManager.csListItemHeight,
                    csListItemBorderColor = appearanceManager.csListItemBorderColor,
                    csListItemTitleColor = appearanceManager.csListItemTitleColor,
                    csListItemMargin = appearanceManager.csListItemMargin,
                    customFont = appearanceManager.csCustomFont,
                    quality = appearanceManager.csCoverQuality,
                    customItem = appearanceManager.csListItemInterface
                )
            }
        }
        return res
    }

    override fun onBindViewHolder(holder: StoriesListItem, position: Int) {
        if (holder is FavoriteListItem) {
            holder.bind(favoriteImages = favoriteImages)
            holder.itemView.setOnClickListener {
            }
        } else {
            holder.bind(storyData = stories[position])
        }
    }

    override fun getItemCount(): Int {
        return if (favoriteImages.isNullOrEmpty()) {
            stories.size
        } else {
            stories.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        var pref = position * 10
        return if (!favoriteImages.isNullOrEmpty() && position == stories.size)
            pref + 3
        else {
            stories[position].videoCoverUrl()?.let {
                pref += 5
            }
            if (stories[position].isOpened())
                pref + 2
            else
                pref + 1
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    init {

    }
}