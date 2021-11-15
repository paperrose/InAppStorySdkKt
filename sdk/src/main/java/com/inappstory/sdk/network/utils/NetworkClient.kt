package com.inappstory.sdk.network.utils

import android.os.Build
import com.inappstory.sdk.network.callbacks.NetworkCallback
import com.inappstory.sdk.utils.json.JsonParser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class NetworkClient {
    var jsonParser: JsonParser = JsonParser()

    fun getUrl(request: Request): URL {
        var qVars = ""
        if (!request.queryFields.isNullOrEmpty()) {
            for (key: String in request.queryFields.keys) {
                qVars += "&" + key + "=" + request.queryFields[key]
            }
            qVars = "?" + qVars.substring(1)
        }
        return URL(request.url + qVars)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun enqueue(request: Request, callback: NetworkCallback?) {
        GlobalScope.launch {
            execute(request, callback)
        }
    }

    fun encode(value: String?): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
            } catch (e: UnsupportedEncodingException) {
            }
        }
        return value
    }

    fun execute(request: Request, callback: NetworkCallback?) {
        val connection = getUrl(request)
            .openConnection() as HttpURLConnection
        connection.connectTimeout = 30000
        connection.readTimeout = 30000
        connection.requestMethod = request.getMethod()

        for (key: String in request.headers.keys) {
            connection.setRequestProperty(key, request.headers[key])
        }
        if (request.isFormEncoded) {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }
        if (request.bodyFields.isNotEmpty()) {
            for (key : String in request.bodyFields.keys) {
                request.body += "&" + key + "=" + encode(request.bodyFields[key])
            }
            if (request.body.isNotEmpty() && request.body.startsWith("&")) {
                request.body = request.body.substring(1)
            }
        }
        if (request.requestType != RequestType.GET && request.body.isNotEmpty()) {
            if (!request.isFormEncoded) {
                connection.setRequestProperty("Content-Type", "application/json")
            }
            connection.doOutput = true
            val outStream = connection.outputStream
            val outStreamWriter = OutputStreamWriter(outStream, "UTF-8")
            outStreamWriter.write(request.body)
            outStreamWriter.flush()
            outStreamWriter.close()
            outStream.close()
        }
        val statusCode = connection.responseCode
        if (callback != null)
            if (statusCode == 200 || statusCode == 201 || statusCode == 202) {
                callback.onSuccess(
                    if (callback.getClass() == null) {
                        callback.onSuccess(null)
                    } else {
                        jsonParser.jsonToPOJO(
                            getResponseFromStream(connection.inputStream),
                            callback.getClass()!!, callback.isList()
                        )
                    }
                )
            } else {
                getResponseFromStream(connection.errorStream)?.let {
                    callback.onFailure(
                        statusCode,
                        it
                    )
                }
            }
        connection.disconnect()
    }

    fun getResponseFromStream(inputStream: InputStream?): String? {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var inputLine: String?
        val response = StringBuffer()
        while (bufferedReader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        bufferedReader.close()
        return response.toString()
    }
}