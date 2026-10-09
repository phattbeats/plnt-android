package com.plnt.client.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.plnt.client.stream.StreamViewPhase
import com.plnt.client.stream.StreamViewState
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.Sage
import kotlinx.coroutines.delay
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSink

/**
 * PHA-3289: full-screen viewer for one TS6 screen share. The session itself
 * lives in VoiceService; this screen only owns the renderer and hands it over
 * as the sink while it is on screen. Controls sit on a top gradient and hide
 * themselves after a few seconds; tapping the video brings them back.
 */
@Composable
fun StreamViewerScreen(
    view: StreamViewState,
    eglContext: EglBase.Context?,
    onAttachSink: (VideoSink?) -> Unit,
    onStop: () -> Unit,
) {
    BackHandler(onBack = onStop)

    // A movie night is exactly the case the screen timeout would ruin.
    val androidView = LocalView.current
    DisposableEffect(Unit) {
        androidView.keepScreenOn = true
        onDispose { androidView.keepScreenOn = false }
    }

    var controlsVisible by remember { mutableStateOf(true) }
    var controlsNonce by remember { mutableStateOf(0) }
    val playing = view.phase == StreamViewPhase.PLAYING
    LaunchedEffect(controlsNonce, playing) {
        if (playing) {
            delay(3500)
            controlsVisible = false
        } else {
            controlsVisible = true
        }
    }

    val status = when (view.phase) {
        StreamViewPhase.IDLE, StreamViewPhase.JOINING -> "Asking to join…"
        StreamViewPhase.CONNECTING -> "Connecting to the streamer…"
        StreamViewPhase.PLAYING -> "${view.width}×${view.height}"
        StreamViewPhase.ENDED -> view.detail ?: "Stream ended"
        StreamViewPhase.FAILED -> view.detail ?: "Could not connect"
    }
    val statusColor = when (view.phase) {
        StreamViewPhase.PLAYING -> Sage
        StreamViewPhase.FAILED -> Oxblood
        StreamViewPhase.ENDED -> BoneMuted
        else -> Gold
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                controlsVisible = true
                controlsNonce++
            },
    ) {
        if (eglContext != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    SurfaceViewRenderer(ctx).apply {
                        init(eglContext, null)
                        setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                        setEnableHardwareScaler(true)
                        onAttachSink(this)
                    }
                },
                onRelease = { renderer ->
                    onAttachSink(null)
                    renderer.release()
                },
            )
        }

        if (!playing) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (view.phase) {
                    StreamViewPhase.ENDED, StreamViewPhase.FAILED -> Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Videocam, contentDescription = null, tint = statusColor, modifier = Modifier.size(32.dp))
                    }
                    else -> CircularProgressIndicator(color = Gold, strokeWidth = 3.dp, modifier = Modifier.size(44.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(status, color = Bone, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                if (view.phase == StreamViewPhase.FAILED || view.phase == StreamViewPhase.ENDED) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SecondaryButton("Back to channel", onClick = onStop, color = Bone)
                }
            }
        }

        AnimatedVisibility(
            visible = controlsVisible || !playing,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        view.stream?.name ?: "Stream",
                        style = MaterialTheme.typography.titleMedium,
                        color = Bone,
                        maxLines = 1,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(statusColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(status, style = MaterialTheme.typography.labelMedium, color = statusColor)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onStop),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Stop watching", tint = Bone)
                }
            }
        }
    }
}
