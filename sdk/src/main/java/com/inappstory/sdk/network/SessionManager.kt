package com.inappstory.sdk.network

import com.inappstory.sdk.api.data.models.OpenSessionResponse
import com.inappstory.sdk.api.data.models.SessionData
import com.inappstory.sdk.api.data.models.StatisticSessionObject
import com.inappstory.sdk.network.callbacks.EmptyCallback
import com.inappstory.sdk.network.callbacks.SessionOpenCallback

class SessionManager(private val requestGenerator: RequestGenerator? = null) {
    private val openCallbacks : ArrayList<SessionOpenCallback> = ArrayList()
    private val openCallbacksLock = Any()

    private fun addCallback(callback: SessionOpenCallback) {
        synchronized(openCallbacksLock) {
            openCallbacks.add(callback)
        }
    }

    private var isOpening: Boolean = false


    fun closeSession(data: StatisticSessionObject?, callback: EmptyCallback) {
        SessionData.instance = null
        requestGenerator?.closeSession(data, callback)
    }

    fun checkSession(callback: SessionOpenCallback) {
        if (SessionData.instance != null) {
            callback.onSuccess(null)
        } else {
            addCallback(callback)
            if (!isOpening) {
                requestGenerator?.openSession(object : SessionOpenCallback() {
                    override fun onSuccess(response: Any?) {
                        (response as OpenSessionResponse).saveSession()
                        synchronized(openCallbacksLock) {
                            openCallbacks.forEach {
                                it.onSuccess(null)
                            }
                            openCallbacks.clear()
                        }
                    }

                    override fun onFailure(status: Int, error: String) {
                        synchronized(openCallbacksLock) {
                            openCallbacks.forEach {
                                it.onFailure(status, error)
                            }
                            openCallbacks.clear()
                        }
                    }
                })
            }
        }
    }
}