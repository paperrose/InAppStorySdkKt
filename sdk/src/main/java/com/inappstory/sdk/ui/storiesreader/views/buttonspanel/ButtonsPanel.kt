package com.inappstory.sdk.ui.storiesreader.views.buttonspanel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.inappstory.sdk.databinding.CsButtonsPanelBinding

class ButtonsPanel(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: CsButtonsPanelBinding? = null
    private val binding get() = _binding!!

    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

    val viewModel: ButtonsPanelViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[ButtonsPanelViewModel::class.java]
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel?.likeObservable()?.observeForever(likeObserver)
        viewModel?.favoriteObservable()?.observeForever(favoriteObserver)
        viewModel?.subscribe()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel?.likeObservable()?.removeObserver(likeObserver)
        viewModel?.favoriteObservable()?.removeObserver(favoriteObserver)
        viewModel?.unsubscribe()
    }

    private fun likeClick() {
        binding.likeButton.isEnabled = false
        binding.dislikeButton.isEnabled = false
        viewModel?.storyLike()
    }

    private fun dislikeClick() {
        binding.likeButton.isEnabled = false
        binding.dislikeButton.isEnabled = false
        viewModel?.storyDislike()
    }

    private fun favoriteClick() {
        binding.favoriteButton.isEnabled = false
        viewModel?.storyFavorite()
    }

    fun shareClick() {

    }

    fun soundClick() {

    }

    private val favoriteObserver = Observer<Pair<Boolean, Boolean>> {
        binding.favoriteButton.isActivated = it.first
        binding.favoriteButton.isEnabled = true
    }

    private val likeObserver = Observer<Pair<Int, Boolean>> {
        binding.likeButton.isActivated = (it.first == 1)
        binding.likeButton.isEnabled = true
        binding.dislikeButton.isActivated = (it.first == -1)
        binding.dislikeButton.isEnabled = true
    }

    init {
        _binding = CsButtonsPanelBinding.inflate(
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        )
        binding.likeButton.setOnClickListener {
            likeClick()
        }
        binding.dislikeButton.setOnClickListener {
            dislikeClick()
        }
        binding.favoriteButton.setOnClickListener {
            favoriteClick()
        }
    }


}