package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.SerializedName

class ResourceMappingObject(url: String, key: String, type: String?, index: Int?) {
    @SerializedName("url")
    var url: String = url
    @SerializedName("key")
    var key: String = key
    @SerializedName("type")
    var type: String? = type
    @SerializedName("slide_index")
    var index: Int? = index

    constructor() : this("", "", null, 0)
}