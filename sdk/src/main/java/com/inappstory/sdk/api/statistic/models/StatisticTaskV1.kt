package com.inappstory.sdk.api.statistic.models

class StatisticTaskV1(var startTime: Long? = null,
                      var storyId: String? = null,
                      var slideIndex: Int? = null,
                      var type: Int? = null,
                      var endTime: Long? = null,
                      var slides: List<Int>? = null) {

}