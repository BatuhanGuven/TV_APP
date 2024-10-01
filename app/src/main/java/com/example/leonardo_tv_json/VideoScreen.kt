package com.example.leonardo_tv_json

import AnalogClock
import AnalogClockModifier
import LeftSideBox
import ScrollingSubtitles
import VideoPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoScreen(viewModel: VideoViewModel) {
    val context = LocalContext.current
    val videos by viewModel.videoList.collectAsState()
    val uiConfigurationResponse by viewModel.uiConfiguration.collectAsState()

    var isPanelVisible by remember { mutableStateOf(false) }

    LaunchedEffect(videos, uiConfigurationResponse) {
        viewModel.fetchVideos()
        viewModel.fetchUiConfiguration()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .onPreviewKeyEvent { keyEvent ->

                if (keyEvent.type == KeyEventType.KeyUp &&
                    (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter)
                ) {

                    isPanelVisible = !isPanelVisible
                    true
                } else {
                    false
                }
            }
    ) {
        if (uiConfigurationResponse != null && videos.isNotEmpty()) {
            val uiConfigurationData = uiConfigurationResponse!!.UIConfiguration
            val leftSideBox = uiConfigurationData.LeftSideBox
            val analogClock = uiConfigurationData.AnalogClock
            val videoPlayer = uiConfigurationData.VideoPlayer
            val scrollingSubtitles = uiConfigurationData.ScrollingSubtitles

            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItems = videos.map { video ->
                        MediaItem.fromUri(video.url)
                    }
                    setMediaItems(mediaItems)
                    prepare()
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                }
            }

            DisposableEffect(exoPlayer) {
                onDispose {
                    exoPlayer.release()
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                ) {
                    Row {
                        LeftSideBox(leftSideBox, analogClock)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            VideoPlayer(exoPlayer = exoPlayer, videoPlayer = videoPlayer)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Blue)
                ) {
                    ScrollingSubtitles(scrollingSubtitles, leftSideBox)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        AnimatedVisibility(visible = isPanelVisible) {
            SlidingPanel(viewModel)
        }
    }
}

@Composable
fun SlidingPanel(viewModel: VideoViewModel) {
    var ipAddress by remember { mutableStateOf("") }
    var savedIpAddress by remember{ mutableStateOf("")}
    var usageIp  = viewModel.savedIpAddress.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val offsetX by animateDpAsState(
        targetValue = if (true) screenWidth - 300.dp else screenWidth
    )


    val textFieldFocusRequester = remember { FocusRequester() }
    val buttonFocusRequester = remember { FocusRequester() }


    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Gray.copy(alpha = 0.8f))
            .padding(16.dp)
            .onPreviewKeyEvent { keyEvent ->
                when {
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {

                        buttonFocusRequester.requestFocus()
                        true
                    }

                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {

                        textFieldFocusRequester.requestFocus()
                        true
                    }

                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionCenter -> {

                        if (buttonFocusRequester.captureFocus()) {

                            viewModel.savedIpAddress.value = ipAddress
                            savedIpAddress = ipAddress
                            val baseUrl = "http://$ipAddress:3000/"
                            viewModel.updateRepositoryBaseUrl(baseUrl)
                            viewModel.isPanelVisible.value = false
                        }
                        true

                    }

                    else -> false
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sunucu Adresi Girin", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Sunucu Adresi") },
                placeholder = { Text("IP") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(textFieldFocusRequester)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.savedIpAddress.value = ipAddress
                    val baseUrl = "http://${ipAddress}:3000/"
                    viewModel.updateRepositoryBaseUrl(baseUrl)
                    viewModel.isPanelVisible.value = false
                },
                modifier = Modifier.focusRequester(buttonFocusRequester)
            ) {
                Text("Kaydet")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (usageIp.value.isNotEmpty()) {
                Text("Kullanılan Sunucu Adresi: ${usageIp.value}")
            }
        }
    }
}

/*logo serverdan çekilecek*/
@Composable
fun LeftSideBox(leftSideBox: LeftSideBox, analogClock: AnalogClock) {
    Box(
        modifier = Modifier
            .width(leftSideBox.box.width.toInt().dp)
            .fillMaxHeight(leftSideBox.box.height.length.toFloat())
            .background(Color(android.graphics.Color.parseColor(leftSideBox.box.backgroundColor)))
            .padding(leftSideBox.box.padding.toInt().dp)
    ) {
        Column {
            Box(modifier = Modifier
                .width(leftSideBox.box.width.toInt().dp)
                .fillMaxHeight(leftSideBox.box.height.toFloat())){
                Image(
                    painter = rememberImagePainter(leftSideBox.image.url),
                    contentDescription = "Company Logo",
                    modifier = Modifier
                        .fillMaxSize(leftSideBox.image.modifier.fillWidth.toFloat())
                        .padding(bottom = leftSideBox.image.modifier.padding.toInt().dp)
                )
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                elevation = 20.dp,
                shape = RoundedCornerShape(16.dp)
            ){
                AnalogClock(analogClock = analogClock, analogClockModifier = leftSideBox.analogClock)
            }
        }
    }
}

@Composable
fun VideoPlayer(exoPlayer: ExoPlayer,videoPlayer: VideoPlayer) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(videoPlayer.width.toFloat())
            .fillMaxHeight(videoPlayer.height.toFloat()),
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    )
}
@Composable
fun ScrollingSubtitles(
    scrollingSubtitles: ScrollingSubtitles,
    leftSideBox: LeftSideBox
) {
    val subtitles = scrollingSubtitles.subtitles.list
    val scrollSpeed = scrollingSubtitles.scrollSettings.scrollSpeed.value
    val textColor = Color(android.graphics.Color.parseColor(scrollingSubtitles.appearance.textColor.value))
    val backgroundColor = Color(android.graphics.Color.parseColor(scrollingSubtitles.appearance.backgroundColor.value))

    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            scrollState.scrollBy(scrollSpeed.toFloat() * 0.1f)
            val maxScrollValue = scrollState.layoutInfo.totalItemsCount * 1000f
            if (scrollState.firstVisibleItemScrollOffset >= maxScrollValue) {
                scrollState.scrollToItem(0)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = scrollState,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items(Int.MAX_VALUE) { index ->
                if (index % 2 == 0) {
                    Text(
                        text = subtitles[(index / 2) % subtitles.size],
                        color = textColor,
                        fontSize = scrollingSubtitles.appearance.textSize.value.toInt().sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(data = leftSideBox.image.url),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 8.dp)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalogClock(analogClock: AnalogClock, analogClockModifier: AnalogClockModifier) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(TimeZone.getTimeZone(analogClock.timeZone.value))) }
    val clockModifier = analogClockModifier.modifier

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance(TimeZone.getTimeZone(analogClock.timeZone.value))
            delay(1000L)
        }
    }

    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    val second = currentTime.get(Calendar.SECOND)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor(analogClock.clockAppearance.backgroundColor.value))),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(analogClock.clockAppearance.size.value.toInt().dp)) {
                drawCircle(
                    color = Color(android.graphics.Color.parseColor(analogClock.clockAppearance.circle.borderColor)),
                    radius = analogClock.clockAppearance.circle.radius.toInt().dp.toPx(),
                    style = Stroke(analogClock.clockAppearance.circle.borderWidth.toInt().dp.toPx())
                )

                // Saatin kollarını çiz
                val hourRotation = (hour % 12) * 30f + (minute / 2f)
                val minuteRotation = minute * 6f
                val secondRotation = second * 6f

                // Saat kolu
                drawLine(
                    color = Color(android.graphics.Color.parseColor(analogClock.hands.hourHand.color)),
                    start = center,
                    end = center.copy(
                        x = center.x + 45.dp.toPx() * kotlin.math.cos(Math.toRadians((hourRotation - 90).toDouble())).toFloat(),
                        y = center.y + 45.dp.toPx() * kotlin.math.sin(Math.toRadians((hourRotation - 90).toDouble())).toFloat()
                    ),
                    strokeWidth = analogClock.hands.hourHand.width.toInt().dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Dakika kolu
                drawLine(
                    color = Color(android.graphics.Color.parseColor(analogClock.hands.minuteHand.color)),
                    start = center,
                    end = center.copy(
                        x = center.x + 55.dp.toPx() * kotlin.math.cos(Math.toRadians((minuteRotation - 90).toDouble())).toFloat(),
                        y = center.y + 55.dp.toPx() * kotlin.math.sin(Math.toRadians((minuteRotation - 90).toDouble())).toFloat()
                    ),
                    strokeWidth = analogClock.hands.minuteHand.width.toInt().dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(android.graphics.Color.parseColor(analogClock.hands.secondHand.color)),
                    start = center,
                    end = center.copy(
                        x = center.x + 52.dp.toPx() * kotlin.math.cos(Math.toRadians((secondRotation - 90).toDouble())).toFloat(),
                        y = center.y + 52.dp.toPx() * kotlin.math.sin(Math.toRadians((secondRotation - 90).toDouble())).toFloat()
                    ),
                    strokeWidth = analogClock.hands.secondHand.width.toInt().dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = analogClock.text.city.value,
                fontSize = analogClock.text.city.fontSize.toInt().sp,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor(analogClock.text.city.color))
            )

            Text(
                text = String.format("%02d:%02d", hour, minute),
                fontSize = analogClock.text.timeDisplay.fontSize.toInt().sp,
                fontWeight = FontWeight(analogClock.text.timeDisplay.fontWeight.toInt())
            )

            Text(
                text = analogClock.text.country.value,
                fontSize = analogClock.text.country.fontSize.toInt().sp,
                color = Color(android.graphics.Color.parseColor(analogClock.text.country.color))
            )
        }
    }
}
