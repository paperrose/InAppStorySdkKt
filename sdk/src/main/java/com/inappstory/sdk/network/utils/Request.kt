package com.inappstory.sdk.network.utils

class Request(requestType: RequestType, url: String, isFormEncoded: Boolean) {
    var headers: HashMap<String, String> = HashMap()
    var queryFields: HashMap<String, String> = HashMap()
    var bodyFields: HashMap<String, String> = HashMap()
    var body: String = ""
    var isFormEncoded: Boolean = isFormEncoded
    var url: String = url
    var requestType: RequestType = requestType

    fun isFormEncoded(isEncoded: Boolean): Request {
        isFormEncoded = isEncoded;
        return this
    }

    fun getMethod(): String {
        return requestType.name
    }

    fun addHeader(key: String, value: String?): Request {
        if (!value.isNullOrEmpty())
            headers[key] = value
        return this
    }

    fun url(url: String): Request {
        this.url = url;
        return this
    }

    fun addBodyField(key: String, value: String?): Request {
        if (!value.isNullOrEmpty())
            bodyFields[key] = value
        return this
    }

    fun addQueryField(key: String, value: String?): Request {
        if (!value.isNullOrEmpty())
            queryFields[key] = value
        return this
    }

    fun requestType(type: RequestType): Request {
        this.requestType = type;
        return this
    }

    fun body(value: String): Request {
        this.body = value
        return this
    }
}