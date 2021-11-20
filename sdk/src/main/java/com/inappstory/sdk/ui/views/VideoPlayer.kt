package com.inappstory.sdk.ui.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import java.io.IOException

class VideoPlayer(context: Context, attrs: AttributeSet? = null) :
    TextureView(context, attrs), TextureView.SurfaceTextureListener {

    var isLoaded = false
    var url: String? = null
    var mp: MediaPlayer? = null
    var surface: Surface? = null

    private val videoPlayerVM = VideoPlayerVM(this)


    fun loadVideo(path: String) {

        if (url == null || url != path) {
            videoPlayerVM.file = null
        }
        url = path
        isLoaded = true
        if (this.isAvailable) {
            prepareVideo(surfaceTexture)
        }
        surfaceTextureListener = this
    }

    private fun prepareVideo(t: SurfaceTexture?) {
        surface = Surface(t)
        if (mp == null) mp = MediaPlayer()
        mp?.setSurface(surface)
        try {
            mp?.setDataSource(videoPlayerVM.getSource(url!!))
            mp?.prepareAsync()
            mp?.setOnPreparedListener {
                videoPlayerVM.isMpPrepared = true
                it.isLooping = true
                videoPlayerVM.updateTextureViewSize(
                    width.toFloat(),
                    height.toFloat(),
                    it.videoWidth.toFloat(),
                    it.videoHeight.toFloat()
                )
                it.setVolume(0f, 0f)
                it.start()
            }
        } catch (e1: IllegalArgumentException) {
            e1.printStackTrace()
        } catch (e1: SecurityException) {
            e1.printStackTrace()
        } catch (e1: IllegalStateException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun startPlay(): Boolean {
        mp?.let {
            if (!it.isPlaying) {
                it.start()
                return true
            }
        }
        return false
    }

    fun pausePlay() {
        mp?.pause()
    }

    fun stopPlay() {
        mp?.stop()
    }

    fun changePlayState() {
        mp?.let {
            if (it.isPlaying) it.pause() else it.start()
        }
    }

    fun release() {
        mp?.let {
            it.stop()
            it.reset()
            it.release()
        }
        mp = null
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        videoPlayerVM.isMpPrepared = false
        prepareVideo(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        this.surface?.release()
        release()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

}