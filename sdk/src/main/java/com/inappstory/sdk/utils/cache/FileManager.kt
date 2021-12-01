package com.inappstory.sdk.utils.cache

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

//Main access class
class FileManager(private val lruCacheManager: LruCacheManager) {
    data class GetFileResult(val file: File?, val fromCache: Boolean)

    fun getFile(
        url: String, fast: Boolean, outputFile: File? = null,
        callback: ((Long, Long) -> Unit)? = null
    ): GetFileResult {
        val key: String = url
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


    private val downloadFileExecutor = Executors.newFixedThreadPool(1)

    fun getFileAsync(
        url: String, fast: Boolean, outputFile: File? = null,
        callback: ((Long, Long) -> Unit)? = null
    ): GetFileResult {
        val key: String = url
        val cache = if (fast) lruCacheManager.fastCache else lruCacheManager.commonCache
        cache?.get(key)?.let {
            return GetFileResult(it, true)
        }
        downloadFileExecutor.submit {
            val file: File? =
                FileNetworkDownloader().downloadFile(url,
                    outputFile ?: cache?.getFileFromKey(key), callback)
            file?.let {
                cache?.put(key, it)
            }
        }
        return GetFileResult(null, false)
    }
}