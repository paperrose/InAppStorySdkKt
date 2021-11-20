package com.inappstory.sdk.api.data.models.protocols

interface CachedStoryModelProtocol : CellStoryModelProtocol {
    fun tags(): String?
    fun like(): Boolean
    fun dislike(): Boolean
    fun disableClose(): Boolean
    fun slidesCount(): Int
    fun hasLike(): Boolean
    fun hasFavorite(): Boolean
    fun hasShare(): Boolean
}