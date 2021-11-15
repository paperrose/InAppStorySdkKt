package com.inappstory.sdk.network.callbacks

import com.inappstory.sdk.api.data.models.StoryData
import kotlin.reflect.KClass

abstract class EmptyCallback : NetworkCallback {
    override fun getClass(): KClass<*>? {
        return null
    }

    override fun isList(): Boolean {
        return false
    }


    override fun onSuccess(response : Any?) {}

    override fun onFailure(status : Int, error : String) {}
}