package com.inappstory.sdk.utils.cache

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Decoder {
    private const val HASH_ALGORITHM = "SHA512"

    fun cropUrl(url: String) : String {
        return url.split("\\?")[0]
    }

    fun hash(s: String): String? {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            hexString.toString() + "_u0"
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}