package com.inappstory.sdk.ui.storiesreader

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.inappstory.sdk.databinding.CsStoriesReaderScreenDialogBinding
import com.inappstory.sdk.ui.storiesreader.views.draggable.DraggableLayoutSubscriber
import com.inappstory.sdk.ui.storiesreader.views.draggable.DraggableRepository
import com.inappstory.sdk.ui.storiesreader.views.pager.CubeTransformer
import com.inappstory.sdk.ui.storiesreader.views.pager.StoriesReaderPagerAdapter
import com.inappstory.sdk.ui.storiesreader.views.pager.StoriesReaderPagerCallback

class StoriesReaderScreen() : DialogFragment(), StoriesReaderPagerCallback,
    DraggableLayoutSubscriber, ViewPager.OnPageChangeListener {
    companion object {
        fun newInstance(
            storiesIds: ArrayList<String>,
            settings: String,
            index: Int = 0
        ): StoriesReaderScreen {
            val fragment = StoriesReaderScreen()
            fragment.setArgs(storiesIds, settings, index)
            return fragment
        }

        const val storiesIdsArgKey = "storiesIds"
        const val settingsArgKey = "settings"
        const val indexArgKey = "index"
    }

    fun setArgs(storiesIds: ArrayList<String>, settings: String, index: Int = 0) {
        arguments = Bundle()
        arguments?.putStringArrayList(storiesIdsArgKey, storiesIds)
        arguments?.putString(settingsArgKey, settings)
        arguments?.putInt(indexArgKey, index)
    }

    private var _binding: CsStoriesReaderScreenDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StoriesReaderScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DraggableRepository.csIsDraggable(requireArguments().getBoolean("csIsDraggable", false))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[StoriesReaderScreenViewModel::class.java]
        _binding = CsStoriesReaderScreenDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        binding.storiesReaderPager.callback = null
        super.onDestroyView()
        _binding = null

    }

    val storiesIds = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.storiesReaderPager.callback = this
        storiesIds.clear()
        storiesIds.addAll(requireArguments().getStringArrayList(storiesIdsArgKey)!!)
        binding.storiesReaderPager.adapter =
            StoriesReaderPagerAdapter(
                childFragmentManager,
                requireArguments().getString(settingsArgKey)!!,
                storiesIds
            )
        binding.storiesReaderPager.currentItem = requireArguments().getInt(indexArgKey)
        binding.storiesReaderPager.setPageTransformer(true, CubeTransformer())
        binding.draggableLayout.viewModel?.subscribers?.add(this)
        viewModel.initManagers(ArrayList(requireArguments().getStringArrayList(storiesIdsArgKey)))
        binding.storiesReaderPager.addOnPageChangeListener(this)
        val ind = requireArguments().getInt(indexArgKey)
        if (ind > 0) {
            binding.storiesReaderPager.currentItem = ind
        } else {
            onPageSelected(0)
        }
        viewModel.currentStoryIndex.observe(viewLifecycleOwner, {
            if (it >= storiesIds.size) dismiss()
            binding.storiesReaderPager.setCurrentItem(it, true)
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onStart() {
        super.onStart()

        // safety check
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun swipeUp(currentItem: Int?) {
    }

    override fun swipeDown(currentItem: Int?) {
        currentItem?.let {
            dismiss()
        }
    }

    override fun swipeLeft(currentItem: Int) {
    }

    override fun swipeRight(currentItem: Int) {
    }

    override fun onPageScrolled(currentItem: Int, offset: Float, offsetPixels: Int) {
    }

    override fun onPageSelected(currentItem: Int) {
        viewModel.loadStory(currentItem)
        viewModel.setCurrentIdLiveData(storiesIds[currentItem])
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onDrag(
        elasticOffset: Float,
        elasticOffsetPixels: Float,
        rawOffset: Float,
        rawOffsetPixels: Float
    ) {

    }

    override fun onDragDismissed() {
        dismiss()
    }

    override fun onDragDropped() {

    }

    override fun touchPause() {

    }

    override fun touchResume() {

    }
}