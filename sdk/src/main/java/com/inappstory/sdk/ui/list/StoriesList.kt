package com.inappstory.sdk.ui.list

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.inappstory.sdk.AppearanceManager
import com.inappstory.sdk.R
import com.inappstory.sdk.utils.dispatchers.StoriesListsDispatcher

class StoriesList(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    constructor(context: Context, isFavoriteList: Boolean)
            : this(context) {
        this.isFavoriteList = isFavoriteList
    }

    var appearanceManager = AppearanceManager()
    var isFavoriteList = false

    private fun addListeners() {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        StoriesListsDispatcher.addSubscriber(manager)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        StoriesListsDispatcher.removeSubscriber(manager)
    }

    private val manager = StoriesListManager()

    init {
        attrs?.let {
            val typedArray: TypedArray =
                context.obtainStyledAttributes(it, R.styleable.StoriesList)
            isFavoriteList = typedArray.getBoolean(R.styleable.StoriesList_cs_listIsFavorite,
                false)
            typedArray.recycle()
        }
        addListeners()
    }
}