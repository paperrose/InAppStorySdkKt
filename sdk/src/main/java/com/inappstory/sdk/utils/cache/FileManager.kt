package com.inappstory.sdk.utils.cache

import java.io.File

//Main access class
class FileManager(private val lruCacheManager: LruCacheManager) {
    data class GetFileResult(val file: File?, val fromCache: Boolean)

    fun getFile(
        url: String, fast: Boolean, outputFile: File? = null,
        callback: ((Long, Long) -> Unit)? = null
    ): GetFileResult {
        val key: String = Decoder.cropUrl(url)
        val cache = if (fast) lruCacheManager.fastCache else lruCacheManager.commonCache
        cache?.get(key)?.let {
            return GetFileResult(it, true)
        }
        val file: File? =
            FileNetworkDownloader().downloadFile(url,
                outputFile ?: cache?.getFileFromKey(key), callback)
        file?.let {
            cache?.put(key, it)
        }
        return GetFileResult(file, false)
    }
}