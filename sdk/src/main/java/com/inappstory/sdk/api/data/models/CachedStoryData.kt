package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.api.data.models.protocols.CachedStoryModelProtocol
import com.inappstory.sdk.utils.json.SerializedName

open class CachedStoryData : CachedStoryModelProtocol {

    @SerializedName("id")
    var id: String

    @SerializedName("title_color")
    var titleColor: String?

    @SerializedName("title")
    var title: String?

    @SerializedName("tags")
    var tags: String?

    @SerializedName("background_color")
    var backgroundColor: String?

    @SerializedName("image")
    var image: ArrayList<Image>?

    @SerializedName("video_cover")
    var videoUrl: ArrayList<Image>?


    @SerializedName("like")
    var like: Int?

    @SerializedName("slides_count")
    var slidesCount: Int?

    @SerializedName("favorite")
    var favorite: Boolean?

    @SerializedName("hide_in_reader")
    var hideInReader: Boolean?

    @SerializedName("deeplink")
    var deeplink: String?

    @SerializedName("is_opened")
    var isOpened: Boolean?

    @SerializedName("disable_close")
    var disableClose: Boolean?

    @SerializedName("like_functional")
    var hasLike: Boolean?

    @SerializedName("has_audio")
    var hasAudio: Boolean?

    @SerializedName("favorite_functional")
    var hasFavorite: Boolean?

    @SerializedName("share_functional")
    var hasShare: Boolean?

    constructor() : this(
        "", null,
        null, null, null, null, null,
        0, 0, false, false, null, false, false,
        false, false, false, false
    )

    constructor(
        id: String,
        titleColor: String?,
        title: String?,
        tags: String?,
        backgroundColor: String?,
        image: ArrayList<Image>?,
        videoCover: ArrayList<Image>?,
        like: Int?,
        slidesCount: Int?,
        favorite: Boolean?,
        hideInReader: Boolean?,
        deeplink: String?,
        isOpened: Boolean?,
        disableClose: Boolean?,
        hasLike: Boolean?,
        hasAudio: Boolean?,
        hasFavorite: Boolean?,
        hasShare: Boolean?
    ) {
        this.id = id
        this.titleColor = titleColor
        this.title = title
        this.tags = tags
        this.backgroundColor = backgroundColor
        this.image = image
        this.videoUrl = videoCover
        this.like = like
        this.slidesCount = slidesCount
        this.favorite = favorite
        this.hideInReader = hideInReader
        this.deeplink = deeplink
        this.isOpened = isOpened
        this.disableClose = disableClose
        this.hasLike = hasLike
        this.hasAudio = hasAudio
        this.hasFavorite = hasFavorite
        this.hasShare = hasShare
    }

    override fun id(): String {
        return id
    }


    override fun title(): String {
        var tmp = title ?: ""
        return tmp
    }

    override fun titleColor(): String {
        return titleColor ?: "#000000"
    }

    override fun deeplink(): String? {
        return deeplink
    }

    override fun tags(): String? {
        return tags
    }

    override fun backgroundColor(): String {
        return backgroundColor ?: "#000000"
    }

    override fun imageCover(quality: ImageQuality?): Image? {
        if (image.isNullOrEmpty()) return null
        var q: String? = "m"
        if (quality != null && quality == ImageQuality.HIGH) {
            q = "h"
        }
        image!!.forEach {
            if (it.type.equals(q)) return it
        }
        return image!![0]
    }

    override fun images(): ArrayList<Image>? {
        return image
    }

    override fun videoCoverUrl(): String? {
        return if (videoUrl.isNullOrEmpty()) null
        else videoUrl!![0].url
    }

    override fun slidesCount(): Int {
        return slidesCount ?: 0
    }

    override fun like(): Boolean {
        return (like ?: 0) == 1
    }

    override fun dislike(): Boolean {
        return (like ?: 0) == -1
    }

    override fun favorite(): Boolean {
        return (favorite ?: 0) == 1
    }

    override fun isOpened(): Boolean {
        return (isOpened ?: 0) == 1
    }

    override fun disableClose(): Boolean {
        return (disableClose ?: 0) == 1
    }

    override fun hasLike(): Boolean {
        return (hasLike ?: 0) == 1
    }

    override fun hasAudio(): Boolean {
        return (hasAudio ?: 0) == 1
    }

    override fun hasFavorite(): Boolean {
        return (hasFavorite ?: 0) == 1
    }

    override fun hasShare(): Boolean {
        return (hasShare ?: 0) == 1
    }

    override fun hideInReader(): Boolean {
        return (hideInReader ?: 0) == 1
    }


}
