package com.example.leonardo_tv_json

import UIConfiguration
import UIConfigurationResponse
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider


class VideoViewModel(private var repository: VideoRepository) : ViewModel() {

    private val _videoList = MutableStateFlow<List<Video>>(emptyList())
    val videoList: StateFlow<List<Video>> = _videoList

    private val _uicConfigurationResponse = MutableStateFlow<UIConfigurationResponse?>(null)
    val uiConfiguration: StateFlow<UIConfigurationResponse?> = _uicConfigurationResponse

    var isPanelVisible = MutableStateFlow<Boolean>(false)

    var savedIpAddress = MutableStateFlow("")

    fun fetchVideos() {
        viewModelScope.launch {
            try {
                val videos = repository.getVideosFromServer()
                _videoList.value = videos
            } catch (e: Exception) {
                _videoList.value = emptyList()
            }
        }
    }

    fun fetchUiConfiguration() {
        viewModelScope.launch {
            try {
                val uiConfiguration = repository.getUiConfigurationFromServer()
                _uicConfigurationResponse.value = uiConfiguration
            } catch (e: Exception) {
                _uicConfigurationResponse.value = null
            }
        }
    }

    fun updateRepositoryBaseUrl(newBaseUrl: String) {
        repository = VideoRepository(newBaseUrl)
        _videoList.value = emptyList()
        _uicConfigurationResponse.value = null
        fetchVideos()
        fetchUiConfiguration()
    }
}

class VideoViewModelFactory(private val videoRepository: VideoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            return VideoViewModel(videoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

