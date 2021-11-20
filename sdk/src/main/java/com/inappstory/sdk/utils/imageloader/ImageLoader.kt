package com.inappstory.sdk.utils.imageloader

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.inappstory.sdk.utils.cache.FileManager
import com.inappstory.sdk.utils.cache.LruCacheManager
import com.inappstory.sdk.utils.common.ThreadNavigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageLoader {
    private var memoryCache: LruCache<String, Bitmap>

    fun displayImage(
        url: String,
        imageView: ImageView,
        diskCache: LruCacheManager,
        fast: Boolean = true,
        placeholderId: Int? = null,
        backgroundColor: Int? = null
    ) {
        memoryCache[url]?.also {
            imageView.setImageBitmap(it)
        } ?: run {
            placeholderId?.let {
                imageView.setImageResource(it)
            }
            GlobalScope.launch {
                val result = FileManager(diskCache).getFile(url, fast = fast)
                result.let {
                    it.file?.let { file ->
                        BitmapWorker().decodeSampledBitmapFromFile(
                            file,
                            800, 800
                        )?.also { bmp ->
                            memoryCache.put(url, bmp)
                            ThreadNavigator.runInUiThread {
                                imageView.setImageBitmap(bmp)
                            }
                            return@launch
                        }
                    }
                    backgroundColor?.let { bgColor ->
                        ThreadNavigator.runInUiThread {
                            imageView.setBackgroundColor(bgColor)
                        }
                    }

                }
            }
        }
    }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        memoryCache = object : LruCache<String, Bitmap>(maxMemory / 8) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }
}