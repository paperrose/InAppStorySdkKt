package com.inappstory.sdk.utils.cache

import android.util.Log
import com.inappstory.sdk.utils.file.FileSystemWorker
import java.io.*
import java.util.*
import kotlin.collections.HashMap

class CacheJournal(private val fileSystemWorker: FileSystemWorker, private val cacheDir: File) {

    var journalLock = Any()
    private val journalVersion = 1
    private val itemLinks = HashMap<String, JournalItem>()
    var currentSize: Long = 0
    lateinit var journal: File

    init {
        readJournal()
    }


    private fun readJournal() {
        synchronized(journalLock) {
            journal = fileSystemWorker.getJournalFile(cacheDir)
            if (!journal.exists() || journal.length() == 0L) return
            val stream = DataInputStream(FileInputStream(journal))
            val version = stream.readShort().toInt()

            if (version == journalVersion) {
                val count = stream.readInt()
                var localSize: Long = 0
                for (c in 0 until count) {
                    val key = stream.readUTF()
                    val name = stream.readUTF()
                    val time = stream.readLong()
                    val size = stream.readLong()
                    localSize += size
                    val item = JournalItem(key, name, time, size)
                    putLink(item)
                }
                currentSize = localSize
            } else {
                Log.e(
                    "InAppStory_SDK", "Invalid journal ${journal.canonicalPath} format version"
                )
            }
            stream.close()
        }
    }

    fun writeJournal() {
        synchronized(journalLock) {
            val stream = DataOutputStream(FileOutputStream(journal))
            stream.writeShort(journalVersion)
            stream.writeInt(itemLinks.size)
            itemLinks.values.forEach {
                stream.writeUTF(it.key)
                stream.writeUTF(it.name)
                stream.writeLong(it.time!!)
                stream.writeLong(it.size!!)
            }
        }
    }

    fun put(item: JournalItem, cacheSize: Long) {
        synchronized(journalLock) {
            val fileSize: Long = item.size!!
            removeOld(fileSize, cacheSize)
            putLink(item)
        }
    }

    fun get(key: String?): JournalItem? {
        val item: JournalItem? = itemLinks[key]
        updateTime(item)
        return item
    }

    fun delete(key: String, withFile: Boolean): JournalItem? {
        val item: JournalItem? = itemLinks.remove(key)
        item?.let {
            currentSize -= it.size!!
            if (withFile) fileSystemWorker.delete(cacheDir, it.name)
        }
        return item
    }

    private fun removeOld(newFileSize: Long, limitSize: Long) {
        if (currentSize + newFileSize > limitSize) {
            val items = ArrayList(itemLinks.values)
            Collections.sort(items, TimeComparator())
            for (i in items.size - 1 downTo 1) {
                val item: JournalItem = items.removeAt(i)
                fileSystemWorker.delete(cacheDir, item.name)
                itemLinks.remove(item.key)
                currentSize -= item.size!!
                if (currentSize + newFileSize < limitSize) {
                    break
                }
            }
        }
    }

    private fun putLink(item: JournalItem) {
        itemLinks[item.key] = item
        currentSize += item.size!!
    }

    fun keySet(): Set<String> {
        return Collections.unmodifiableSet(itemLinks.keys)
    }

    private fun updateTime(item: JournalItem?) {
        item?.time = System.currentTimeMillis()
    }
}