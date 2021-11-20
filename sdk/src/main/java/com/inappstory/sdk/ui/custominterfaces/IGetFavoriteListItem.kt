package com.inappstory.sdk.ui.custominterfaces

import android.content.Context
import android.view.View
import com.inappstory.sdk.ui.list.models.FavoriteImage

interface IGetFavoriteListItem {
    /**
     * Use to initialize or inflate favorite cell view, that will be used in list
     */
    fun getFavoriteItem(context: Context): View?

    /**
     * @param favCell        (favCell) is a RelativeLayout, which contains the View returned by getFavoriteItem method. If you need to access the internal View directly - you must firstly set an id for it or access it as  favCell.getChildAt(0)
     * @param favoriteImages (favoriteImages) contains list of covers for favorite stories in [FavoriteImage] type.
     * @param count          (count) contains a size of favorite images list
     */
    fun bindFavoriteItem(favCell: View, favoriteImages: List<FavoriteImage>?, count: Int)
}