package com.inappstory.sdk.api.statistic.models

import com.inappstory.sdk.utils.json.SerializedName

class StatisticTaskV2(
    var event: String? = null,

    @SerializedName("ts")
    var timestamp: Long? = null,

    @SerializedName("u")
    var userId: String? = null,

    @SerializedName("s")
    var sessionId: String? = null,

    @SerializedName("i")
    var storyId: String? = null,

    @SerializedName("w")
    var whence: String? = null,

    @SerializedName("t")
    var target: String? = null,

    @SerializedName("c")
    var cause: String? = null,

    @SerializedName("si")
    var slideIndex: Int? = null,

    @SerializedName("st")
    var slideTotal: Int? = null,

    @SerializedName("d")
    var durationMs: Long? = 0L,

    @SerializedName("wi")
    var widgetId: String? = null,

    @SerializedName("wl")
    var widgetLabel: String? = null,

    @SerializedName("wv")
    var widgetValue: String? = null,

    @SerializedName("wa")
    var widgetAnswer: Int? = null,

    @SerializedName("wal")
    var widgetAnswerLabel: String? = null,

    @SerializedName("was")
    var widgetAnswerScore: Int? = null,

    @SerializedName("li")
    var layoutIndex: Int? = null
)