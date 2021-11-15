package com.inappstory.sdk.api.data.models

import com.inappstory.sdk.utils.json.SerializedName

class OpenSessionResponse(
    session: SessionData?,
    serverTimestamp: Long?,
    cachedFonts: ArrayList<CachedFontObject>?,
    placeholders: ArrayList<StoryPlaceholder>?,
    allowProfiling: Boolean?,
    allowStatV1: Boolean?,
    allowStatV2: Boolean?
) {
    @SerializedName("session")
    var session: SessionData? = session
    @SerializedName("server_timestamp")
    var serverTimestamp: Long? = serverTimestamp
    @SerializedName("cache")
    var cachedFonts: ArrayList<CachedFontObject>? = cachedFonts
    @SerializedName("placeholders")
    var placeholders: ArrayList<StoryPlaceholder>? = placeholders
    @SerializedName("is_allow_profiling")
    var isAllowProfiling: Boolean? = allowProfiling
    @SerializedName("is_allow_statistic_v1")
    var isAllowStatV1: Boolean? = allowStatV1
    @SerializedName("is_allow_statistic_v2")
    var isAllowStatV2: Boolean? = allowStatV2

    constructor() : this(null, 0, null, null,
        true, false, false)

    fun saveSession() {
        session?.isAllowProfiling = isAllowProfiling
        session?.isAllowStatV1 = isAllowStatV1
        session?.isAllowStatV2 = isAllowStatV2
        session?.save()
    }

}