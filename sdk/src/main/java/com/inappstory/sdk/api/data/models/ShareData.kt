package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.SerializedName

class ShareData(
    url: String,
    title: String?,
    description: String?
) {
    @SerializedName("url")
    var url: String = url
    @SerializedName("title")
    var title: String? = title
    @SerializedName("description")
    var description: String? = description


    constructor() : this("", null, null) {
    }
}