package com.inappstory.sdk.api.statistic.models

class ProfilingTask(
    var uniqueHash: String? = null,
    var name: String? = null,
    var startTime: Long = 0,
    var endTime: Long = 0,
    var sessionId: String? = null,
    var userId: String? = null,
    var type: Int = 0 //0 for timings
) {

    override fun toString(): String {
        return "ProfilingTask{" +
                "uniqueHash='" + uniqueHash + '\'' +
                ", name='" + name + '\'' +
                ", endTime=" + endTime +
                '}'
    }
}