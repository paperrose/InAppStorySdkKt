package com.inappstory.inappstorysdk.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import com.inappstory.inappstorysdk.R
import com.inappstory.inappstorysdk.databinding.TestFragmentBinding
import com.inappstory.sdk.utils.common.Sizes

class TestFragment : Fragment() {

    companion object {
        fun newInstance() = TestFragment()
    }

    private lateinit var viewModel: TestViewModel
    private lateinit var binding: TestFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.settings.loadWithOverviewMode = true;
        binding.webView.settings.useWideViewPort = true;
        binding.webView.loadUrl("https://cdn.pixabay.com/photo/2021/08/25/20/42/field-6574455_960_720.jpg")
        with(binding.motionLayout) {
            getConstraintSet(R.id.allStates).constrainHeight(
                R.id.movable_view,
                Sizes.getScreenSize(context).y
            )
        }
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == motionLayout!!.endState) {
                    motionLayout.transitionToState(R.id.full_end)
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[TestViewModel::class.java]
        // TODO: Use the ViewModel
    }

}