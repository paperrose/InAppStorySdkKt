package com.inappstory.sdk.ui.list

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inappstory.sdk.AppearanceManager
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.R
import com.inappstory.sdk.api.data.models.StoryData
import com.inappstory.sdk.api.data.models.protocols.CellStoryModelProtocol
import com.inappstory.sdk.network.callbacks.GetStoriesListCallback
import com.inappstory.sdk.ui.list.models.FavoriteImage
import com.inappstory.sdk.utils.common.ThreadNavigator
import com.inappstory.sdk.utils.dispatchers.StoriesListsDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class StoriesList(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    var adapter: StoriesAdapter? = null

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

    constructor(context: Context, isFavoriteList: Boolean)
            : this(context) {
        this.isFavoriteList = isFavoriteList

    }


    var appearanceManager = AppearanceManager()
    var isFavoriteList = false

    fun loadStories() {
        InAppStoryManager.apiWorker?.let {
            it.getStoriesList(null,
                getStoriesListCallback = object : GetStoriesListCallback() {
                    override fun onSuccess(response: Any?) {
                        val stories = ArrayList<CellStoryModelProtocol>()
                        (response as ArrayList<StoryData>).forEach { story ->
                            stories.add(story)
                            InAppStoryManager.storiesStorage?.updateStory(story)
                        }
                        val favImages: ArrayList<FavoriteImage> = ArrayList()
                        if (isFavoriteList) {
                            ThreadNavigator.runInUiThread {
                                adapter =
                                    StoriesAdapter(
                                        appearanceManager,
                                        stories
                                    )
                                manager.adapter = adapter
                                setAdapter(adapter)
                            }
                        } else {
                            it.getStoriesFavImages(object : GetStoriesListCallback() {
                                override fun onSuccess(favResponse: Any?) {
                                    (favResponse as ArrayList<StoryData>).forEach { story ->
                                        favImages.add(
                                            FavoriteImage(
                                                story.id(),
                                                story.image,
                                                story.backgroundColor()
                                            )
                                        )
                                    }
                                    ThreadNavigator.runInUiThread {
                                        adapter =
                                            StoriesAdapter(
                                                appearanceManager,
                                                stories,
                                                favImages
                                            )
                                        manager.adapter = adapter
                                        setAdapter(adapter)
                                    }

                                }

                                override fun onFailure(status: Int, error: String) {
                                    TODO("Not yet implemented")
                                }
                            })
                        }

                    }

                    override fun onFailure(status: Int, error: String) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun addListeners() {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        manager.subscribe()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        manager.unsubscribe()
    }

    private val manager = StoriesListManager()

    init {
        attrs?.let {
            val typedArray: TypedArray =
                context.obtainStyledAttributes(it, R.styleable.StoriesList)
            isFavoriteList = typedArray.getBoolean(
                R.styleable.StoriesList_cs_listIsFavorite,
                false
            )
            typedArray.recycle()
        }
        layoutManager = LinearLayoutManager(getContext(), HORIZONTAL, false)
        addListeners()
    }
}