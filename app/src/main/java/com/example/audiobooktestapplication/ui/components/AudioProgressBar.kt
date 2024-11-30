package com.example.audiobooktestapplication.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.audiobooktestapplication.util.formatTime

@Composable
fun AudioProgressBar(
    currentTime: Long,
    totalTime: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSeconds = currentTime / 1000
    val totalSeconds = totalTime / 1000

    val animatedValue by animateFloatAsState(
        targetValue = currentSeconds.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        ), label = "animatedValue"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = formatTime(currentSeconds))
        Slider(
            value = animatedValue,
            onValueChange = { onSeek((it * 1000).toLong()) },
            valueRange = 0f..totalSeconds.toFloat(),
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
        )
        Text(text = formatTime(totalSeconds))
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AudioProgressBarPreview() {
    AudioProgressBar(
        currentTime = 28000L,
        totalTime = 132000L,
        onSeek = {  }
    )
}