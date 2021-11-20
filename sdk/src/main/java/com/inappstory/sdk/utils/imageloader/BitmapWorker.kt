package com.inappstory.sdk.utils.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream

class BitmapWorker {
    fun decodeSampledBitmapFromFile(
        f: File,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return when {
            !f.exists() -> null
            else -> BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(f), null, this)
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                inJustDecodeBounds = false
                val fileInputStream = FileInputStream(f)
                val bitmap = BitmapFactory.decodeStream(fileInputStream, null, this)
                fileInputStream.close()
                bitmap
            }
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}