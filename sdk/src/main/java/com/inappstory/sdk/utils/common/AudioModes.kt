package com.inappstory.sdk.utils.common

import android.content.Context
import android.media.AudioManager

object AudioModes {
    private fun getModeVal(modeName: String?): Int {
        return when (modeName) {
            MODE_CALL_SCREENING -> 4
            MODE_CURRENT -> -1
            MODE_INVALID -> -2
            MODE_IN_CALL -> 2
            MODE_IN_COMMUNICATION -> 3
            MODE_RINGTONE -> 1
            else -> 0
        }
    }

    const val MODE_CALL_SCREENING = "MODE_CALL_SCREENING"
    const val MODE_CURRENT = "MODE_CURRENT"
    const val MODE_INVALID = "MODE_INVALID"
    const val MODE_IN_CALL = "MODE_IN_CALL"
    const val MODE_IN_COMMUNICATION = "MODE_IN_COMMUNICATION"
    const val MODE_NORMAL = "MODE_NORMAL"
    const val MODE_RINGTONE = "MODE_RINGTONE"

    fun setAudioManagerMode(mode: String?, context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = getModeVal(mode)
    }
}