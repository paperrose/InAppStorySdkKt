package com.inappstory.sdk.ui.storiesreader.page

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.data.loader.OnSlideLoaded
import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import com.inappstory.sdk.api.dispatchers.DispatcherSubscriber
import com.inappstory.sdk.databinding.CsStoriesReaderPageBinding
import com.inappstory.sdk.ui.storiesreader.StoriesReaderScreenViewModel
import com.inappstory.sdk.ui.storiesreader.views.webview.StoriesWebViewMessages
import com.inappstory.sdk.utils.common.AudioModes
import com.inappstory.sdk.utils.common.Sizes
import com.inappstory.sdk.utils.html.WebPageConverter

class ReaderPageFragment : Fragment(), DispatcherSubscriber {
    companion object {
        fun newInstance(
            storyId: String,
            settings: String,
            index: Int = 0
        ): ReaderPageFragment {
            val fragment = ReaderPageFragment()
            fragment.setArgs(storyId, settings, index)
            return fragment
        }

        const val storyIdArgKey = "storyId"
        const val settingsArgKey = "settings"
        const val indexArgKey = "index"
    }

    fun setArgs(storyId: String, settings: String, index: Int = 0, storyIndex: Int = 0) {
        arguments = Bundle()
        arguments?.putString(storyIdArgKey, storyId)
        arguments?.putString(settingsArgKey, settings)
        arguments?.putInt(indexArgKey, index)
    }


    private var _binding: CsStoriesReaderPageBinding? = null
    private val binding get() = _binding!!
    var timerData: MutableLiveData<Pair<Int, Long>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        readerViewModel =
            ViewModelProvider(requireActivity())[StoriesReaderScreenViewModel::class.java]
        _binding = CsStoriesReaderPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    val storyData = MutableLiveData<ReaderStoryDataProtocol>()
    var durations = ArrayList<Int>()

    private lateinit var readerViewModel: StoriesReaderScreenViewModel

    var storyId: String = "0"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
        durations.clear()
        storyId = requireArguments().getString(storyIdArgKey)!!

        InAppStoryManager.storiesStorage?.getStory(storyId)
            ?.let {
                durations = ArrayList(it.slidesCount())
            }

        setTimelineDurations(durations)
        binding.storiesView.initSettings()
        binding.storiesView.setClient(requireContext())
        timerData = binding.timeline.storiesTimer.getObservableData()
        timerData?.observe(viewLifecycleOwner, timerDataObserver)
        storyData.observe(viewLifecycleOwner, storyDataObserver)
        readerViewModel.getCurrentIdLiveData().observe(viewLifecycleOwner, currentIdObserver)
        readerViewModel.getStory(storyId, storyData)
    }

    var slidesCount: Int = 1

    private val storyDataObserver = Observer<ReaderStoryDataProtocol> {
        durations.clear()
        durations.addAll(it.durations()!!)
        slidesCount = durations.size
        setTimelineDurations(durations)
        _binding?.storiesView!!.readerPageManager.id = storyId
        openSlide(readerViewModel.getLastIndex(storyId))
        //startSlide()
    }

    private val timerDataObserver = Observer<Pair<Int, Long>> {
        if (isActive) {
            _binding?.timeline?.setSlideCompleted(it.first)
            _binding?.timeline?.viewModel?.clearTimer()
            if (it.first + 1 < durations.size)
                openSlide(it.first + 1)
        }
        //Toast.makeText(context, "${it.first} ${it.second}", Toast.LENGTH_LONG).show()
    }

    var isActive: Boolean = false

    private val currentIdObserver = Observer<String> {
        Log.e("currentId", "$storyId $it")
        if (storyId == it) {
            isActive = true
            _binding?.timeline?.setActive(true)
            readerViewModel.setCurrentSlideDownload(storyId, readerViewModel.getLastIndex(storyId))
            _binding?.storiesView?.startSlide()
        } else {
            _binding?.timeline?.setActive(false)
            _binding?.storiesView?.stopSlide()
            isActive = false
        }

        //Toast.makeText(context, "${it.first} ${it.second}", Toast.LENGTH_LONG).show()
    }

    private fun setTimelineDurations(durations: ArrayList<Int>) {
        val localDurations = ArrayList<Long>()
        durations.forEach {
            localDurations.add(it.toLong())
        }
        _binding?.timeline?.storiesTimer?.setDurations(localDurations)
        _binding?.timeline?.initSegments()
    }

    var converter = WebPageConverter()


    fun startTimeline(index: Int = 0) {
        _binding?.timeline?.viewModel?.startTimer(index = index)
        _binding?.timeline?.setCurrentSlide(index)
    }

    var openedIndex = -1

    private fun openSlide(index: Int = 0) {
        Log.e("slideData_openSlide", "$storyId $index")
        if (isActive) {
            Log.e("slideData_isActive", "$storyId $index")
        }
        _binding?.storiesView!!.readerPageManager.slideIndex = index
        //  openedIndex = index
        readerViewModel.setLastIndex(storyId, index)
        readerViewModel.getSlide(storyId, index, object : OnSlideLoaded {
            override fun onSlideLoaded(storyId: String, index: Int) {
                Handler(Looper.getMainLooper()).post {
                    storyData.value?.let {
                        var pageData = it.page(index)!!
                        pageData = if (pageData.contains("<video")) {
                            converter.replaceVideoAndLoad(pageData, it, index)
                        } else {
                            converter.replaceImagesAndLoad(pageData, it, index)
                        }
                        _binding?.storiesView?.loadData(
                            it.layout()?.replace("{{%content}}", pageData),
                            pageData
                        )
                    }

                }
            }

            override fun onSlideError(storyId: String, index: Int) {

            }
        })

    }

    fun nextSlide() {
        val ind = readerViewModel.getLastIndex(storyId)
        if (ind >= slidesCount - 1) {
            _binding?.timeline?.viewModel?.clearTimer()
            readerViewModel.currentStoryIndex.postValue(readerViewModel.getStoryIndex(storyId) + 1)
            return
        }
        openSlide(ind + 1)
    }

    fun prevSlide() {
        val ind = readerViewModel.getLastIndex(storyId)
        val storyInd = readerViewModel.getStoryIndex(storyId)
        if (ind == 0) {
            if (storyInd == 0) {
                _binding?.timeline?.viewModel?.restartTimer()
            } else {
                readerViewModel.currentStoryIndex.postValue(readerViewModel.getStoryIndex(storyId) - 1)
                _binding?.timeline?.viewModel?.clearTimer()
            }
            return
        }
        openSlide(ind - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerData?.removeObserver(timerDataObserver)
        storyData.removeObserver(storyDataObserver)
        unsubscribe()
        _binding = null
    }

    override fun notify(message: Array<Any?>?) {
        message?.let {
            when (it[0]) {
                StoriesWebViewMessages.SLIDE_LOADED -> {
                    if (isActive)
                        _binding?.storiesView!!.startSlide()
                }
                StoriesWebViewMessages.SLIDE_STARTED -> {
                    startTimeline(readerViewModel.getLastIndex(storyId))
                }
                StoriesWebViewMessages.SLIDE_CLICK -> {
                    slideClick(it[1] as String?, it[2] == false)
                }
                StoriesWebViewMessages.SET_AUDIO_MODE -> {
                    AudioModes.setAudioManagerMode(it[1] as String, requireContext())
                }
                StoriesWebViewMessages.CHANGE_INDEX -> {
                    openSlide(it[1] as Int)
                }
                StoriesWebViewMessages.OPEN_GAME -> {

                }
                StoriesWebViewMessages.API_REQUEST -> {

                }
                StoriesWebViewMessages.SEND_WIDGET_STAT -> {
                    InAppStoryManager.apiWorker!!.statisticManagerV2.sendWidgetStoryEvent(
                        it[1] as String?,
                        it[2] as String?
                    )
                }
                else -> {
                    readerViewModel.notify(message)
                }
            }
        }
    }

    private fun slideClick(payload: String?, notForbidden: Boolean) {
        payload?.let {
            return
        }
        val size = Sizes.getScreenSize(requireContext()).x
        if (_binding?.storiesView!!.viewModel!!.clickCoordinate >= 0.3f * size &&
            notForbidden
        ) {
            nextSlide()
        } else if (_binding?.storiesView!!.viewModel!!.clickCoordinate < 0.3f * size) {
            prevSlide()
        }
    }

    override fun error(message: Array<Any?>?) {

    }

    override fun subscribe() {
        _binding?.storiesView!!.readerPageManager.subscribers.add(this)
    }

    override fun unsubscribe() {
        _binding?.storiesView!!.readerPageManager.subscribers.remove(this)
    }
}