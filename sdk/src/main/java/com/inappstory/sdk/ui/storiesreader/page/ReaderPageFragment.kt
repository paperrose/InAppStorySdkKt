package com.inappstory.sdk.ui.storiesreader.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inappstory.sdk.databinding.CsStoriesReaderPageBinding
import com.inappstory.sdk.ui.storiesreader.views.timeline.TimerRepository

class ReaderPageFragment : Fragment() {
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

    fun setArgs(storyId: String, settings: String, index: Int = 0) {
        arguments = Bundle()
        arguments?.putString(storyIdArgKey, storyId)
        arguments?.putString(settingsArgKey, settings)
        arguments?.putInt(indexArgKey, index)
    }


    private var _binding: CsStoriesReaderPageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CsStoriesReaderPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timerRepository = TimerRepository()
        timerRepository.setDurations(arrayListOf(10000L, 3000L, 4000L))
        binding.timeline.timerRepository = timerRepository
        binding.timeline.initSegments()
        binding.timeline.setCurrentSlide(1)
        timerRepository.startTimer(index = 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}