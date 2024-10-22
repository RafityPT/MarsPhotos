package com.example.marsphotos.network

import retrofit2.Retrofit
import retrofit2.http.GET
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType


private const val BASE_URL = "https://picsum.photos"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface PicsumApiService {
    //este get nao sei se esta correto
    @GET("/v2/list")
    suspend fun getListPicsumPhotos(): List<PicsumPhoto>
}


object PicsumApi {
    val retrofitService : PicsumApiService by lazy {
        retrofit.create(PicsumApiService::class.java)
    }
}