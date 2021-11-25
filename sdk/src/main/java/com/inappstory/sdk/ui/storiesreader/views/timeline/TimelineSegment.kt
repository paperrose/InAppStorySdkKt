package com.inappstory.sdk.ui.storiesreader.views.timeline

class TimelineSegment(var index: Int) {
    var state: TimelineSegmentState = TimelineSegmentState.EMPTY

    enum class TimelineSegmentState {
        FILLED, EMPTY, ANIMATED
    }
}