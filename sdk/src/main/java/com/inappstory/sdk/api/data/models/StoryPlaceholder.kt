package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.SerializedName

class StoryPlaceholder(id: String?, expireIn: Int?, updatedAt: Long?) {
    var id: String? = id

    @SerializedName("expire_in")
    val expireIn: Int? = expireIn
    var updatedAt: Long? = updatedAt

    constructor() : this(null, 0, 0)
}