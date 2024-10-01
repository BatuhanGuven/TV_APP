package com.example.leonardo_tv_json

import UIConfigurationResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface VideoApiService {

    @GET("uploads/videos")
    suspend fun getVideos(): List<Video>

    @GET("uploads/uiconfig")
    suspend fun getUIConfiguration(): UIConfigurationResponse
}

class VideoRepository(private val baseUrl: String) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(VideoApiService::class.java)

    suspend fun getVideosFromServer(): List<Video> {
        return try {
            api.getVideos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUiConfigurationFromServer(): UIConfigurationResponse? {
        return try {
            api.getUIConfiguration()
        } catch (e: Exception) {
            null
        }
    }
}

