package com.inappstory.sdk

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object InAppStoryManager {
    var uId: String? = null
    fun setUserId(uId: String?) {
        if (uId.isNullOrEmpty()) return
        this.uId = uId
    }
    var context: Context? = null

}