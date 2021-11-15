package com.inappstory.sdk.network.callbacks

import kotlin.reflect.KClass


interface NetworkCallback {
    fun onSuccess(response : Any?)
    fun onFailure(status : Int, error : String)
    fun getClass() : KClass<*>?
    fun isList() : Boolean
}