package com.example.audiobooktestapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.audiobooktestapplication.R

@Composable
fun PlayerControllerComponent(
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onRewind: () -> Unit,
    onPlayPause: () -> Unit,
    onForward: () -> Unit,
    onNext: () -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
    isPlaying: Boolean,
    currentSpeed: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                val newSpeed = if (currentSpeed == 1f) 2f else 1f
                onChangePlaybackSpeed(newSpeed)
            }
        ) {
            Text(text = "x${currentSpeed.toInt()}")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerControlButton(
                icon = painterResource(id = R.drawable.ic_play_back_chapter),
                contentDescription = "Previous",
                onClick = onPrevious
            )

            PlayerControlButton(
                icon = painterResource(id = R.drawable.ic_rewind_back),
                contentDescription = "Rewind",
                onClick = onRewind
            )

            PlayerControlButton(
                icon = painterResource(
                    id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                onClick = onPlayPause
            )

            PlayerControlButton(
                icon = painterResource(id = R.drawable.ic_rewind_forward),
                contentDescription = "Forward",
                onClick = onForward
            )

            PlayerControlButton(
                icon = painterResource(id = R.drawable.ic_play_forward_chapter),
                contentDescription = "Next",
                onClick = onNext
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerControlsPreview() {
    PlayerControllerComponent(
        onPrevious = {},
        onRewind = {},
        onPlayPause = {},
        onForward = {},
        onNext = {},
        onChangePlaybackSpeed = {},
        isPlaying = false,
        currentSpeed = 1f,
    )
}