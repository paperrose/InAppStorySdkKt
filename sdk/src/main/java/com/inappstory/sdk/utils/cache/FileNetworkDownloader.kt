package com.inappstory.sdk.utils.cache

import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FileNetworkDownloader {
    fun downloadFile(
        url: String,
        outputFile: File?,
        callback: ((Long, Long) -> Unit)? = null
    ): File? {
        if (outputFile == null) return null
        outputFile.parentFile?.mkdirs()
        if (!outputFile.exists()) outputFile.createNewFile()
        val urlS = URL(url)
        val urlConnection = urlS.openConnection()
        urlConnection.connectTimeout = 300000
        urlConnection.readTimeout = 300000
        urlConnection.connect()
        val totalLength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            urlConnection.contentLengthLong else
            urlConnection.contentLength.toLong()

        urlS.openStream().use { input ->
            FileOutputStream(outputFile).use { output ->
                val lock = output.channel.lock()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead = input.read(buffer)
                var bytesCopied = 0L
                while (bytesRead >= 0) {
                    output.write(buffer, 0, bytesRead)
                    bytesCopied += bytesRead
                    callback?.invoke(bytesCopied, totalLength)
                    bytesRead = input.read(buffer)
                }
                lock.release()
                output.flush()

                return outputFile
            }
        }
    }
}