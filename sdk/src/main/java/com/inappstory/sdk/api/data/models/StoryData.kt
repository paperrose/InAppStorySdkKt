package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.utils.json.SerializedName

class StoryData : CachedStoryData, ReaderStoryDataProtocol {

    @SerializedName("src_list")
    var srcList: ArrayList<ResourceMappingObject>?

    @SerializedName("slides_duration")
    var durations: ArrayList<Int>?

    @SerializedName("slides_html")
    var pages: ArrayList<String>?

    @SerializedName("layout")
    var layout: String?


    constructor() : this(
        "", null,
        null, null, null, null, null, null,
        0, 0, false, false, null, false, false,
        false, false, false, false, null,
        null, null
    )

    constructor(
        id: String,
        titleColor: String?,
        title: String?,
        tags: String?,
        backgroundColor: String?,
        image: ArrayList<Image>?,
        videoCover: ArrayList<Image>?,
        srcList: ArrayList<ResourceMappingObject>?,
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
        hasShare: Boolean?,
        durations: ArrayList<Int>?,
        pages: ArrayList<String>?,
        layout: String?
    ) : super(
        id, titleColor,
        title, tags,
        backgroundColor, image,
        videoCover, like,
        slidesCount, favorite,
        hideInReader, deeplink,
        isOpened, disableClose,
        hasLike, hasAudio,
        hasFavorite, hasShare
    ) {
        this.srcList = srcList
        this.durations = durations
        this.pages = pages
        this.layout = layout
    }

    override fun layout(): String? {
        return layout
    }

    override fun pages(): ArrayList<String>? {
        return pages
    }

    override fun page(index: Int): String? {
        if (pages != null && index < pages!!.size) return pages!![index]
        return null
    }

    override fun durations(): ArrayList<Int>? {
        return durations
    }

    override fun duration(index: Int): Int {
        if (durations != null && index < durations!!.size) return durations!![index]
        return 0
    }

    override fun srcList(): ArrayList<ResourceMappingObject> {
        if (srcList == null) srcList = ArrayList()
        return srcList!!
    }

    override fun srcListKeys(index : Int, type : String?): ArrayList<String> {
        val res = ArrayList<String>()
        if (srcList == null) return res
        srcList?.forEach {
            if (it.index == index && (type == null || it.type.equals(type))
            ) res.add(it.key)
        }
        return res
    }

    override fun srcListUrls(index : Int, type : String?): ArrayList<String> {
        val res = ArrayList<String>()
        if (srcList == null) return res
        srcList?.forEach {
            if (it.index == index && (type == null || it.type.equals(type))
            ) res.add(it.url)
        }
        return res
    }

}
