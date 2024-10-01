package com.example.leonardo_tv_json

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.leonardo_tv_json.ui.theme.Leonardo_tv_jsonTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoRepository = VideoRepository("https://10.0.2.2:3000/")
        val factory = VideoViewModelFactory(videoRepository)
        val videoViewModel = ViewModelProvider(this,factory).get(VideoViewModel::class.java)

        setContent {
            VideoScreen(viewModel = videoViewModel)
        }
    }
}