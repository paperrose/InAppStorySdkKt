package com.inappstory.inappstorysdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inappstory.inappstorysdk.ui.main.TestFragment

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TestFragment.newInstance())
                .commitNow()
        }
    }
}