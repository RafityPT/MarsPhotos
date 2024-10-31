package com.example.marsphotos.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarsPhoto(
    var id: String = "",
    @SerialName(value = "img_src")
    var imgSrc: String = ""
) {
    // Getters
    fun getMarsId(): String = id
    fun getMarsImgSrc(): String = imgSrc

    // Setters
    fun setMarsId(newId: String) {
        id = newId
    }

    fun setMarsImgSrc(newImgSrc: String) {
        imgSrc = newImgSrc
    }
}
