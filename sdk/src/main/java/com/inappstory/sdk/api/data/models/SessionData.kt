package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.Ignore
import com.inappstory.sdk.utils.json.SerializedName

class SessionData(id: String, expireIn: Int?, updatedAt: Long?)
{
    var id: String = id
    @SerializedName("expire_in")
    var expireIn: Int? = expireIn
    var updatedAt: Long? = updatedAt
    @Ignore
    var isAllowProfiling: Boolean? = false
    @Ignore
    var isAllowStatV1: Boolean? = false
    @Ignore
    var isAllowStatV2: Boolean? = false

    constructor() : this("", 0, 0)

    fun save() {
        instance = this
    }

    fun isAllowStatV2() : Boolean {
        return isAllowStatV2 ?: false
    }

    fun isAllowStatV1() : Boolean {
        return isAllowStatV1 ?: false
    }

    companion object {
        var instance: SessionData? = null
    }
}