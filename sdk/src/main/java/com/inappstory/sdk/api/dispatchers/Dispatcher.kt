package com.inappstory.sdk.api.dispatchers

open class Dispatcher {
    val subscribers = HashSet<DispatcherSubscriber>()

    fun success(message: Array<Any?>?) {
        subscribers.forEach {
            it.notify(message)
        }
    }

    fun error(message: Array<Any?>?) {
        subscribers.forEach {
            it.error(message)
        }
    }
}