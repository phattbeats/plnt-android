package com.plnt.client.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.plnt.client.stream.StreamViewPhase
import com.plnt.client.stream.StreamViewState
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.SurfaceDark
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSink

/**
 * PHA-3289: full-screen viewer for one TS6 screen share. The session itself
 * lives in VoiceService; this screen only owns the renderer and hands it over
 * as the sink while it is on screen.
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

    val status = when (view.phase) {
        StreamViewPhase.IDLE, StreamViewPhase.JOINING -> "asking to join…"
        StreamViewPhase.CONNECTING -> "connecting to the streamer…"
        StreamViewPhase.PLAYING -> "${view.width}×${view.height}"
        StreamViewPhase.ENDED -> view.detail ?: "stream ended"
        StreamViewPhase.FAILED -> view.detail ?: "failed"
    }
    val statusColor = when (view.phase) {
        StreamViewPhase.PLAYING -> BoneMuted
        StreamViewPhase.FAILED -> Oxblood
        StreamViewPhase.ENDED -> BoneMuted
        else -> Gold
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                title = {
                    Column {
                        Text(
                            view.stream?.name ?: "Stream",
                            style = MaterialTheme.typography.titleMedium,
                            color = Bone,
                            maxLines = 1,
                        )
                        Text(status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                },
                actions = {
                    IconButton(onClick = onStop) {
                        Icon(Icons.Filled.Close, contentDescription = "Stop watching", tint = Mauve)
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(androidx.compose.ui.graphics.Color.Black),
            contentAlignment = Alignment.Center,
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
            } else {
                Text("renderer unavailable", color = Oxblood)
            }
            if (view.phase != StreamViewPhase.PLAYING) {
                Text(
                    status,
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
