package com.inappstory.sdk

import android.graphics.Color
import android.graphics.Typeface
import com.inappstory.sdk.api.data.models.ImageQuality
import com.inappstory.sdk.ui.custominterfaces.IGetFavoriteListItem
import com.inappstory.sdk.ui.custominterfaces.IStoriesListItem
import com.inappstory.sdk.ui.custominterfaces.StoryTouchListener
import com.inappstory.sdk.ui.implementations.DefaultFavoriteListItem
import com.inappstory.sdk.ui.implementations.DefaultStoryListItem
import com.inappstory.sdk.utils.common.Sizes

class AppearanceManager(
    var csFavoriteListItemInterface: IGetFavoriteListItem = DefaultFavoriteListItem(),
    var csListItemInterface: IStoriesListItem = DefaultStoryListItem(),
    var csListItemTitleVisibility: Boolean = true,
    var csListItemTitleSize: Int? = -1,
    var csListItemTitleColor: Int? = Color.WHITE,

    var csListItemSourceVisibility: Boolean = false,
    var csListItemSourceSize: Int? = -1,
    var csListItemSourceColor: Int? = Color.WHITE,

    var csListItemWidth: Int? = null,
    var csListItemHeight: Int? = null,

    var csListItemBorderVisibility: Boolean = true,
    var csListItemBorderColor: Int = Color.BLACK,

    var storyTouchListener: StoryTouchListener? = null,

    var csHasLike: Boolean = false,
    var csHasFavorite: Boolean = false,
    var csHasShare: Boolean = false,
    var csTimerGradientEnable: Boolean = true,

    var csFavoriteIcon: Int? = 0,
    var csLikeIcon: Int? = 0,
    var csDislikeIcon: Int? = 0,
    var csShareIcon: Int? = 0,
    var csCloseIcon: Int? = 0,
    var csRefreshIcon: Int? = 0,
    var csSoundIcon: Int? = 0,
    var csNavBarColor: Int = Color.TRANSPARENT,
    var csNightNavBarColor: Int = Color.TRANSPARENT,

    var csCustomFont: Typeface? = null,
    var csCustomBoldFont: Typeface? = null,
    var csCustomItalicFont: Typeface? = null,
    var csCustomBoldItalicFont: Typeface? = null,
    var csCustomSecondaryFont: Typeface? = null,
    var csCustomSecondaryBoldFont: Typeface? = null,
    var csCustomSecondaryItalicFont: Typeface? = null,
    var csCustomSecondaryBoldItalicFont: Typeface? = null,

    var csCoverQuality: ImageQuality = ImageQuality.MEDIUM,

    var csCloseOnSwipe: Boolean = false,
    var csCloseOnOverscroll: Boolean = false,

    var csListItemMargin: Int = Sizes.dpToPxExt(4),
    var csShowStatusBar: Boolean = false,
    var csClosePosition: Int = 2, //1 - topLeft, 2 - topRight, 3 - bottomLeft, 4 - bottomRight;

    var csStoryReaderAnimation: Int = 2,
    var csIsDraggable: Boolean = true

) {
    companion object {
        var instance = AppearanceManager()
    }
}