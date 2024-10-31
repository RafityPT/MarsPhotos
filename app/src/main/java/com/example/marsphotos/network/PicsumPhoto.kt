package com.example.marsphotos.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PicsumPhoto(
    var id: String = "",
    var author: String = "",
    var width: Int = 0,
    var height: Int = 0,
    var url: String = "",
    @SerialName("download_url") var imgSrc: String = ""
) {
    // Getters
    fun getPicsumId(): String = id
    fun getPicsumAuthor(): String = author
    fun getPicsumWidth(): Int = width
    fun getPicsumHeight(): Int = height
    fun getPicsumUrl(): String = url
    fun getPicsumImgSrc(): String = imgSrc

    // Setters
    fun setPicsumId(newId: String) {
        id = newId
    }

    fun setPicsumAuthor(newAuthor: String) {
        author = newAuthor
    }

    fun setPicsumWidth(newWidth: Int) {
        width = newWidth
    }

    fun setPicsumHeight(newHeight: Int) {
        height = newHeight
    }

    fun setPicsumUrl(newUrl: String) {
        url = newUrl
    }

    fun setPicsumImgSrc(newImgSrc: String) {
        imgSrc = newImgSrc
    }
}
