package com.inappstory.sdk.network.callbacks

import com.inappstory.sdk.api.data.models.StoryData
import kotlin.reflect.KClass

abstract class GetStoriesListCallback : NetworkCallback {
    override fun getClass(): KClass<*>? {
        return StoryData::class
    }

    override fun isList(): Boolean {
        return true
    }
}