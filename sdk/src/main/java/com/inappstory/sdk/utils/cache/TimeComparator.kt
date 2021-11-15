package com.inappstory.sdk.utils.cache

import java.util.*

class TimeComparator : Comparator<JournalItem> {
    override fun compare(item1: JournalItem, item2: JournalItem): Int {
        return compare(item2.time!!, item1.time!!)
    }

    private fun compare(x : Long, y : Long) : Int {
        return if (x < y) -1 else if (x == y) 0 else 1
    }
}