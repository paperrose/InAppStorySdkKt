package com.inappstory.sdk.utils.cache

import com.inappstory.sdk.utils.file.FileSystemWorker
import java.io.File

class LruCache(
    var initSize: Long,
    var fileSystemWorker: FileSystemWorker,
    var baseCacheDir: File,
    var subPath: String
) {

    var cacheDir: File? = baseCacheDir
    var cacheSize: Long = initSize
    private var cacheJournal: CacheJournal? = null
    private val journalLock = Any()

    init {
        prepareCacheFolder()
    }

    var prepared: Boolean = false

    private fun prepareCacheFolder() {
        synchronized(journalLock) {
            cacheDir = fileSystemWorker.checkAndUse(baseCacheDir, subPath)
            cacheDir?.let {
                prepared = true
                cacheJournal = CacheJournal(fileSystemWorker, cacheDir!!)
            }
        }
    }



    fun putModel(key: String, json: String) : Boolean {
        synchronized(journalLock) {
            if (!prepared) return false
            keyIsValid(key)
            val path = JsonSaver.putJson(File(cacheDir?.absolutePath + "/" + key), json)
            val item = JournalItem(key, path, System.currentTimeMillis(), 0L)
            cacheJournal!!.delete(key, false)
            cacheJournal!!.put(item, cacheSize)
            cacheJournal!!.writeJournal()
            return true
        }
    }

    fun put(key: String, file: File): File? {
        synchronized(journalLock) {
            if (!prepared) return null
            keyIsValid(key)
            val name = file.absolutePath
            val time = System.currentTimeMillis()
            val fileSize: Long = fileSystemWorker.getFileSize(file)

            val item = JournalItem(key, name, time, fileSize)

            val cacheFile: File? = fileSystemWorker.put(cacheDir!!, file, name)
            cacheJournal!!.delete(key, false)
            cacheJournal!!.put(item, cacheSize)
            cacheJournal!!.writeJournal()
            return cacheFile
        }
    }


    fun delete(key: String) {
        delete(key, true)
    }

    private fun delete(key: String, writeJournal: Boolean) {
        synchronized(journalLock) {
            if (!prepared) return
            keyIsValid(key)
            cacheJournal!!.delete(key, true)?.let {
                if (writeJournal) {
                    cacheJournal!!.writeJournal()
                }
            }
        }
    }

    fun clearCache() {
        synchronized(journalLock) {
            if (!prepared) return
            val keys = cacheJournal!!.keySet()
            keys.forEach {
                delete(it, false)
            }
            cacheJournal!!.writeJournal()
        }
    }

    fun keySet(): Set<String> {
        synchronized(journalLock) {
            if (!prepared) return HashSet()
            return cacheJournal!!.keySet()
        }
    }

    fun cacheSize(): Long {
        synchronized(journalLock) { return cacheSize }
    }

    fun cacheSize(cacheSize: Long) {
        synchronized(journalLock) { this.cacheSize = cacheSize }
    }

    fun getUsedSpace(): Long {
        synchronized(journalLock) {
            if (!prepared) return 0L
            return cacheJournal!!.currentSize
        }
    }

    fun getFreeSpace(): Long {
        synchronized(journalLock) {
            if (!prepared) return 0L
            return cacheSize - cacheJournal!!.currentSize
        }
    }

    fun getJournalSize(): Long {
        synchronized(journalLock) {
            if (!prepared) return 0L
            return cacheJournal!!.journal.length()
        }
    }

    fun getNameFromKey(key: String): String? {
        return Decoder.hash(key)
    }

    fun getFileFromKey(key: String): File? {
        return File(cacheDir?.absolutePath + File.separator + getNameFromKey(key))
    }


    fun hasKey(key: String): Boolean {
        synchronized(journalLock) {

            if (!prepared) return false
            keyIsValid(key)
            return cacheJournal?.get(key) != null
        }
    }

    fun getModel(key: String): String? {
        synchronized(journalLock) {
            if (!prepared) return null
            keyIsValid(key)
            var res: String? = null
            cacheJournal!!.get(key)?.let {
                res = JsonSaver.getJson(File(it.name))
            }
            return res
        }
    }

    fun get(key: String): File? {
        synchronized(journalLock) {
            if (!prepared) return null
            keyIsValid(key)
            var res: File? = null
            cacheJournal!!.get(key)?.let {
                var file: File? = File(it.name)
                if (!file!!.exists()) {
                    cacheJournal!!.delete(key, false)
                    file = null
                }
                cacheJournal!!.writeJournal()
                res = file
            }
            return res
        }
    }

    private fun keyIsValid(key: String) {
        require(!key.isNullOrEmpty()) {
            String.format("Invalid key value: '%s'", key)
        }
    }

}