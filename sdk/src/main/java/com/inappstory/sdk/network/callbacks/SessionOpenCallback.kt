package com.inappstory.sdk.network.callbacks

import com.inappstory.sdk.api.data.models.OpenSessionResponse
import com.inappstory.sdk.api.data.models.SessionData
import com.inappstory.sdk.api.data.models.StoryData
import kotlin.reflect.KClass

abstract class SessionOpenCallback : NetworkCallback {
    override fun getClass(): KClass<*>? {
        return OpenSessionResponse::class
    }

    override fun isList(): Boolean {
        return false
    }
}