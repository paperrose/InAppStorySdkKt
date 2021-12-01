package com.inappstory.sdk.ui.storiesreader.views.webview

import androidx.lifecycle.ViewModel
import com.inappstory.sdk.api.dispatchers.DispatcherSubscriber

class StoriesWebViewModel : ViewModel(), DispatcherSubscriber {

    var clickCoordinate: Float = 0f

    override fun notify(message: Array<Any?>?) {

    }

    override fun error(message: Array<Any?>?) {

    }

    override fun subscribe() {

    }

    override fun unsubscribe() {

    }


}