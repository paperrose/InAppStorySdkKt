package com.inappstory.sdk.api.data.models.protocols

import com.inappstory.sdk.api.data.models.ResourceMappingObject

interface ReaderStoryDataProtocol : CachedStoryModelProtocol {
    fun layout(): String?
    fun pages() : ArrayList<String>?
    fun page(index : Int) : String?
    fun durations() : ArrayList<Int>?
    fun duration(index : Int) : Int
    fun srcList() : ArrayList<ResourceMappingObject>
    fun srcListKeys(index : Int, type : String?) : ArrayList<String>
    fun srcListUrls(index : Int, type : String?) : ArrayList<String>
}