package com.inappstory.sdk.utils.cache

import com.inappstory.sdk.utils.file.FileSystemWorker
import java.io.File

class LruCacheManager(
    fileSystemWorker: FileSystemWorker,
    cacheDir: File
) {

    @Volatile
    var modelsCache: LruCache? = null
    @Volatile
    var fastCache: LruCache? = null
    @Volatile
    var commonCache: LruCache? = null

    init {
        initCaches(fileSystemWorker, cacheDir)
    }

    private fun initCaches(
        fileSystemWorker: FileSystemWorker,
        cacheDir: File
    ) {
        modelsCache = LruCache(StorageSizes.MB_10, fileSystemWorker, cacheDir, "/ias/iasStoryModels")
        var cacheType: Long = StorageSizes.MB_100
        var fastCacheType: Long = StorageSizes.MB_10
        val freeSpace: Long = cacheDir.freeSpace
        if (freeSpace < cacheType + fastCacheType + StorageSizes.MB_10) {
            cacheType = StorageSizes.MB_50
            if (freeSpace < cacheType + fastCacheType + StorageSizes.MB_10) {
                cacheType = StorageSizes.MB_10
                fastCacheType = StorageSizes.MB_5
                if (freeSpace < cacheType + fastCacheType + StorageSizes.MB_10) {
                    cacheType = 0
                }
            }
        }
        fastCache = LruCache(fastCacheType, fileSystemWorker, cacheDir, "/ias/fastCache")
        commonCache = LruCache(cacheType, fileSystemWorker, cacheDir, "/ias/commonCache")
    }

}