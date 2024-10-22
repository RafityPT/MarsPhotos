package com.example.marsphotos.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PicsumPhoto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerialName("download_url") val imgSrc: String // O campo "img_src" do seu código original está representado como "download_url" no JSON
)
