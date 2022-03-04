package com.inappstory.sdk.ui.storiesreader.views.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.inappstory.sdk.ui.storiesreader.page.ReaderPageFragment
import java.util.*
import kotlin.collections.ArrayList

class StoriesReaderPagerAdapter(
    fm: FragmentManager,
    var readerSettings: String,
    var ids: ArrayList<String>
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return ids.size
    }

    override fun getItem(position: Int): Fragment {
        return ReaderPageFragment.newInstance(ids[position],
            readerSettings)
    }
}