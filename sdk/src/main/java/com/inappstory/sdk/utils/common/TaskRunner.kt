package com.inappstory.sdk.utils.common

import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TaskRunner {
    private val executor: Executor =
        Executors.newSingleThreadExecutor() // change according to your requirements

    interface Callback<R> {
        fun onComplete(result: R)
    }

    fun <R> executeAsync(callable: Callable<R>, callback: Callback<R>) {
        executor.execute {
            try {
                val result = callable.call()
                ThreadNavigator.runInUiThread {
                    callback.onComplete(result)
                }
            } catch (e: Exception) {
            }
        }
    }
}