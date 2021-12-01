package com.inappstory.sdk.utils.html

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Base64
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.data.models.protocols.ReaderStoryDataProtocol
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class WebPageConverter {
    fun fromHtml(html: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun replaceImagesAndLoad(
        innerWebData: String, story: ReaderStoryDataProtocol, index: Int
    ): String {
        var data = innerWebData
        val imgs: List<String> = story.srcListUrls(index, null)
        val imgKeys: List<String> = story.srcListKeys(index, null)
        for (i in imgs.indices) {
            val img = imgs[i]
            val imgKey = imgKeys[i]
            val file = InAppStoryManager.cacheManager?.commonCache?.get(img)
            file?.let {
                var fis: FileInputStream? = null
                try {
                    if (it.length() > 0) {
                        fis = FileInputStream(file)
                        val imageRaw = ByteArray(file.length().toInt())
                        fis.read(imageRaw)
                        val cType: String? = null// = KeyValueStorage.getString(file.name)
                        val image64 =
                            if (cType != null) "data:$cType;base64," + Base64.encodeToString(
                                imageRaw,
                                Base64.DEFAULT
                            ) else "data:image/jpeg;base64," + Base64.encodeToString(
                                imageRaw,
                                Base64.DEFAULT
                            )
                        fis.close()
                        data = data.replace(imgKey, image64)
                    } else {
                        data = data.replace(imgKey, img)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return data
    }

    fun replaceVideoAndLoad(
        innerWebData: String, story: ReaderStoryDataProtocol, index: Int
    ): String {
        var data = innerWebData
        val videos: List<String> = story.srcListUrls(index, "video")
        val videosKeys: List<String> = story.srcListKeys(index, "video")
        val cache = InAppStoryManager.cacheManager?.commonCache
        for (i in videos.indices) {
            var video = videos[i]
            val videoKey = videosKeys[i]
            cache?.get(video)?.let {
                video = "file://" + it.absolutePath
            }
            data = data.replace(videoKey, video)
        }
        val imgs: List<String> = story.srcListUrls(index, null)
        val imgKeys: List<String> = story.srcListKeys(index, null)
        for (i in imgs.indices) {
            val img = imgs[i]
            val imgKey = imgKeys[i]
                cache?.get(img)?.let {
                    if (it.exists()) {
                        var fis: FileInputStream? = null
                        try {
                            fis = FileInputStream(it)
                            val imageRaw = ByteArray(it.length().toInt())
                            fis.read(imageRaw)
                            val cType: String? = null//KeyValueStorage.getString(file.name)
                            var image64 = if (cType != null) "data:$cType;base64," + Base64.encodeToString(
                                imageRaw,
                                Base64.DEFAULT
                            ) else "data:image/jpeg;base64," + Base64.encodeToString(
                                imageRaw,
                                Base64.DEFAULT
                            )
                            fis.close()
                            data = data.replace(imgKey, image64)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

        }
        return data
    }
}