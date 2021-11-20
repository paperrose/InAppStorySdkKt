package com.inappstory.sdk.ui.custominterfaces

import android.content.Context
import android.view.View

interface IStoriesListItem {

    /**
     * Use to initialize or inflate simple view, that will be used in list items
     */
    fun getView(context: Context): View

    /**
     * Use to initialize or inflate view with video cover, that will be used in list items
     * To work with video cells, it is recommended to use a class from the VideoPlayer library as
     * a container for displaying video and the loadVideo(String videoUrl) method to launch.
     * This class provides for caching video covers.
     */
    fun getVideoView(context: Context): View

    /**
     * Use to set title for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param title (title) contains a title string from story
     * @param titleColor (titleColor) contains a color for title string
     */
    fun setTitle(itemView: View, title: String?, titleColor: Int?)

    /**
     * Use to set image cover for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param url (url) contains a link to story's cover image.
     * @param backgroundColor (backgroundColor) contains background color to story's cover.
     */
    fun setImage(itemView: View, url: String?, backgroundColor: Int)

    /**
     * Use to check and set sound status if necessary for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param hasAudio (hasAudio) check if current story has audio content.
     */
    fun setHasAudio(itemView: View, hasAudio: Boolean)

    /**
     * Use to set video cover for custom list cell. Use only if getVideoView is set.
     * @param itemView (itemView) contains a view that was initialized in getVideoView() method.
     * @param videoUrl (videoUrl) check if current story has audio content.
     * @param url (url) check if current story has audio content.
     * @param backgroundColor (backgroundColor) check if current story has audio content.
     */
    fun setVideo(itemView: View, videoUrl: String?, url: String?, backgroundColor: Int)

    /**
     * Use to check and set sound status if necessary for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param isOpened (isOpened) check if story was already opened for current user.
     */
    fun setOpened(itemView: View, isOpened: Boolean)

}