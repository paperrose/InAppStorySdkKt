package com.inappstory.sdk.network.callbacks

import com.inappstory.sdk.api.data.models.ShareData
import com.inappstory.sdk.api.data.models.StoryData
import kotlin.reflect.KClass

abstract class StoryShareCallback : NetworkCallback {
    override fun getClass(): KClass<*>? {
        return ShareData::class
    }

    override fun isList(): Boolean {
        return false
    }
}