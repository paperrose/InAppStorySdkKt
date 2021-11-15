package com.inappstory.sdk.utils.cache

import java.io.File
import java.io.FileInputStream
import java.io.FileWriter


object JsonSaver {
    fun putJson(file : File, json : String) : String {
        val writer = FileWriter(
            file.absolutePath
        )
        writer.write(json)
        writer.flush()
        writer.close()
        return file.absolutePath
    }

    fun getJson(file : File) : String {
        val fileInputStream = FileInputStream(file)
        val size: Int = fileInputStream.available()
        val buffer = ByteArray(size)
        fileInputStream.read(buffer)
        fileInputStream.close()
        return String(buffer)
    }
}