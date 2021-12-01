package com.inappstory.sdk.api.dispatchers

interface DispatcherSubscriber {
    fun notify(message: Array<Any?>?)
    fun error(message: Array<Any?>?)
    fun subscribe()
    fun unsubscribe()
}