package com.inappstory.sdk.ui.views

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.utils.cache.FileManager
import java.io.File

class VideoPlayerVM(var videoPlayer: VideoPlayer) {

    var file: File? = null
    var fileManager: FileManager? = null
    var isMpPrepared = false

    fun getSource(url: String): String {
        if (fileManager == null && InAppStoryManager.cacheManager != null)
            fileManager = FileManager(InAppStoryManager.cacheManager!!)
        if (file == null) {
            val result = fileManager!!.getFileAsync(url, fast = true)
            result.file?.let {
                if (!it.exists()) return url
                val fileIsNotLocked = it.renameTo(it)
                if (it.length() > 10 && fileIsNotLocked) {
                    return it.absolutePath
                }
            }
        }
        return url
    }


    fun updateTextureViewSize(
        viewWidth: Float,
        viewHeight: Float,
        mVideoWidth: Float,
        mVideoHeight: Float
    ) {
        var scaleX = 1.0f
        var scaleY = 1.0f
        val coeff1 = viewWidth / viewHeight
        val coeff2 = mVideoWidth / mVideoHeight
        if (mVideoWidth / viewWidth > mVideoHeight / viewHeight) {
            scaleX = coeff2 / coeff1
        } else {
            scaleY = coeff1 / coeff2
        }
        val pivotPointX = viewWidth / 2
        val pivotPointY = viewHeight / 2
        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY)
        videoPlayer.setTransform(matrix)
    }
}