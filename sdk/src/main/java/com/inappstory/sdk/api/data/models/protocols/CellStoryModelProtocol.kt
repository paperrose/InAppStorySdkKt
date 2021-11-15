package com.inappstory.sdk.api.data.models.protocols

import com.inappstory.sdk.api.data.models.Image
import com.inappstory.sdk.api.data.models.ImageQuality

interface CellStoryModelProtocol {
    fun id(): String
    fun title(): String
    fun titleColor(): String
    fun deeplink(): String?
    fun backgroundColor(): String
    fun imageCover(quality: ImageQuality): Image?
    fun videoCoverUrl(): String?
    fun favorite(): Boolean
    fun isOpened(): Boolean
    fun hideInReader(): Boolean
}