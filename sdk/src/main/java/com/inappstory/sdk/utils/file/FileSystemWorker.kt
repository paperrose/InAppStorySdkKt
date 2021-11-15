package com.inappstory.sdk.utils.file

import java.io.File
import java.io.IOException
import java.util.*

class FileSystemWorker() {


    fun checkAndUse(cacheDir: File, subPath: String): File? {
        val file = File(cacheDir.toString() + subPath)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return if (!cacheDir.canWrite()) {
                    null
                } else {
                    val file1 = File(cacheDir.toString() + File.separator + "testFile")
                    if (file1.createNewFile()) {
                        file1.delete()
                        file1
                    } else {
                        null
                    }
                }
            }
        }
        return file
    }


    fun get(cacheDir: File, name: String): File {
        return File(cacheDir, name)
    }

    fun exists(cacheDir: File, name: String): Boolean {
        return File(cacheDir, name).exists()
    }


    fun getJournalFile(cacheDir: File): File {
        val file = File(cacheDir, "journal.bin")
        try {
            cacheDir.mkdirs()
            if (!file.exists()) file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun getFileSize(file: File?): Long {
        if (file == null || !file.exists()) return 0
        if (!file.isDirectory) return file.length()
        val dirs: MutableList<File> = LinkedList()
        dirs.add(file)
        var result: Long = 0
        dirs.forEach {
            if (!it.exists()) return@forEach
            val listFiles = it.listFiles()
            if (listFiles.isNullOrEmpty()) return@forEach
            listFiles.forEach { child ->
                result += child.length()
                if (child.isDirectory) dirs.add(child)
            }
        }
        return result
    }

    fun delete(cacheDir: File, fileName: String) {
        var file = File(cacheDir, fileName)
        if (!file.exists()) {
            file = File(fileName)
        }
        if (!file.exists() || !deleteRecursive(file)) {
            return
        }
    }

    fun deleteRecursive(file : File?) : Boolean {
        var res = true
        if (file == null) return false
        if (file.isDirectory) {
            file.listFiles().forEach {
                res = res and deleteRecursive(it)
            }
        }
        res = res and file.delete()
        return res
    }

    fun put(cacheDir: File, extFile: File, name: String?): File? {
        val newFile = File(name)
        return if ((cacheDir.exists() || cacheDir.mkdirs())
            or newFile.exists()
            or extFile.renameTo(newFile)
        ) {
            newFile
        } else {
            throw formatException("Unable to use file %s", extFile)!!
        }
    }

    private fun formatException(format: String, file: File): IOException? {
        val message = String.format(format, file.name)
        return IOException(message)
    }
}