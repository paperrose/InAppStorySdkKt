package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.SerializedName

class Image(
    url: String,
    width: Int?,
    height: Int?,
    type: String?,
    expire: Int?
) {
    @SerializedName("url")
    var url: String = url

    @SerializedName("width")
    var width: Int? = width

    @SerializedName("height")
    var height: Int? = height

    @SerializedName("type")
    var type: String? = type

    @SerializedName("expire")
    var expire: Int? = expire

    constructor() : this("", 0, 0, null, 0) {
    }
}