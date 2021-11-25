package com.inappstory.sdk.ui.storiesreader.views.buttonspanel

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class ButtonsPanel(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

}