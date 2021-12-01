package com.inappstory.sdk.utils.common

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThreadNavigator {
    var handler: Handler? = null

    fun runInUiThread(func: () -> Unit) {
        if (handler == null)
            handler = Handler(Looper.getMainLooper())
        handler?.post {
            func.invoke()
        }
    }
}