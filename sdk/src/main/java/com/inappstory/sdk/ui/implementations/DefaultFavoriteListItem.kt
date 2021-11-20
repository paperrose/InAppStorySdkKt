package com.inappstory.sdk.ui.implementations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.R
import com.inappstory.sdk.ui.custominterfaces.IGetFavoriteListItem
import com.inappstory.sdk.ui.list.models.FavoriteImage

class DefaultFavoriteListItem : IGetFavoriteListItem {
    var itemWidth: Int = 0
    var itemHeight: Int = 0

    override fun getFavoriteItem(context: Context): View? {
        return LayoutInflater.from(context)
            .inflate(
                R.layout.cs_story_default_list_fav_item,
                null, false
            )
    }

    override fun bindFavoriteItem(
        favCell: View,
        favoriteImages: List<FavoriteImage>?,
        count: Int
    ) {
        if (count > 0) {
            val imageViewLayout =
                favCell.findViewById<RelativeLayout>(R.id.container)
            val image1 = AppCompatImageView(favCell.context)
            val image2 = AppCompatImageView(favCell.context)
            val image3 = AppCompatImageView(favCell.context)
            val image4 = AppCompatImageView(favCell.context)

            val piece2: RelativeLayout.LayoutParams
            val piece3: RelativeLayout.LayoutParams
            val piece4: RelativeLayout.LayoutParams
            when (count) {
                1 -> {
                    image1.layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    setImage(image1, favoriteImages!![0])
                    imageViewLayout.addView(image1)
                }
                2 -> {
                    piece2 = RelativeLayout.LayoutParams(
                        itemWidth/2,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    image1.layoutParams = RelativeLayout.LayoutParams(
                        itemWidth - itemWidth/2,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    image2.layoutParams = piece2
                    setImage(image1, favoriteImages!![0])
                    setImage(image2, favoriteImages[1])
                    imageViewLayout.addView(image1)
                    imageViewLayout.addView(image2)
                }
                3 -> {
                    piece2 = RelativeLayout.LayoutParams(
                        itemWidth/2,
                        itemHeight - itemHeight/2
                    )
                    piece3 = RelativeLayout.LayoutParams(
                        itemWidth/2,
                        itemHeight/2
                    )
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    image1.layoutParams = RelativeLayout.LayoutParams(
                        itemWidth - itemWidth/2,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    image2.layoutParams = piece2
                    image3.layoutParams = piece3
                    setImage(image1, favoriteImages!![0])
                    setImage(image2, favoriteImages[1])
                    setImage(image3, favoriteImages[2])
                    imageViewLayout.addView(image1)
                    imageViewLayout.addView(image2)
                    imageViewLayout.addView(image3)
                }
                else -> {
                    piece2 = RelativeLayout.LayoutParams(
                        itemWidth/2,
                        itemHeight - itemHeight/2
                    )
                    piece3 = RelativeLayout.LayoutParams(
                        itemWidth - itemWidth/2,
                        itemHeight/2
                    )
                    piece4 = RelativeLayout.LayoutParams(
                        itemWidth/2,
                        itemHeight/2
                    )
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    image1.layoutParams = RelativeLayout.LayoutParams(
                        itemWidth - itemWidth/2,
                        itemHeight - itemHeight/2
                    )
                    image2.layoutParams = piece2
                    image3.layoutParams = piece3
                    image4.layoutParams = piece4
                    setImage(image1, favoriteImages!![0])
                    setImage(image2, favoriteImages[1])
                    setImage(image3, favoriteImages[2])
                    setImage(image4, favoriteImages[3])
                    imageViewLayout.addView(image1)
                    imageViewLayout.addView(image2)
                    imageViewLayout.addView(image3)
                    imageViewLayout.addView(image4)
                }
            }
        }
    }

    private fun setImage(imageView: AppCompatImageView, image: FavoriteImage) {
        image.image?.let {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            InAppStoryManager.imageLoader.displayImage(it[0].url, imageView,
                InAppStoryManager.cacheManager!!, true,
                backgroundColor = image.getBackgroundColor())
            return
        }
        imageView.setBackgroundColor(image.getBackgroundColor())
    }
}