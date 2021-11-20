package com.inappstory.sdk.utils.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThreadNavigator {
    fun runInUiThread(func: () -> Unit) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                func.invoke()
            }
        }
    }
}