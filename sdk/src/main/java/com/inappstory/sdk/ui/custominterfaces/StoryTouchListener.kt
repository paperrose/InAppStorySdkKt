package com.inappstory.sdk.ui.custominterfaces

import android.view.View

interface StoryTouchListener {
    fun touchDown(view: View, position: Int)

    fun touchUp(view: View, position: Int)
}