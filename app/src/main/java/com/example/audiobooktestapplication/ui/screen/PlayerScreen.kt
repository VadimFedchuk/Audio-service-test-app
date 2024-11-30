package com.example.audiobooktestapplication.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.audiobooktestapplication.model.BookFileUi
import com.example.audiobooktestapplication.ui.components.AudioProgressBar
import com.example.audiobooktestapplication.ui.components.PlayerControllerComponent
import com.example.audiobooktestapplication.util.PlayerEvent
import com.example.audiobooktestapplication.util.PlayerScreenState

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    state: PlayerScreenState,
    onEvent: (PlayerEvent) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PlayerScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is PlayerScreenState.Error -> {
                Text(
                    text = state.errorMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            is PlayerScreenState.Success -> {
                val context = LocalContext.current
                val imageResId = remember(state.currentBook.posterName) {
                    context.resources.getIdentifier(state.currentBook.posterName, "drawable", context.packageName)
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Book Cover",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Text(
                        text = state.currentBook.fileName,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    AudioProgressBar(
                        currentTime = state.currentBook.currentTime,
                        totalTime = state.currentBook.duration,
                        onSeek = { newTime ->
                            onEvent(PlayerEvent.Seek(newTime))
                        }
                    )

                    PlayerControllerComponent(
                        onPrevious = { onEvent(PlayerEvent.Previous) },
                        onRewind = { onEvent(PlayerEvent.Rewind) },
                        onPlayPause = { onEvent(PlayerEvent.PlayPause) },
                        onForward = { onEvent(PlayerEvent.Forward) },
                        onNext = { onEvent(PlayerEvent.Next) },
                        isPlaying = state.currentBook.isPlaying,
                        onChangePlaybackSpeed = { onEvent(PlayerEvent.ChangePlaybackSpeed(it))},
                        currentSpeed = state.currentBook.playbackSpeed,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenSuccessPreview() {
    PlayerScreen(
        state = PlayerScreenState.Success(
            currentBook = BookFileUi(
                currentTime = 28000L,
                duration = 132000L,
                isPlaying = true,
                chaptersTime = listOf(),
                fileName = "test name",
                posterName = "poster",
                playbackSpeed = 1f,
            )
        ),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenLoadingPreview() {
    PlayerScreen(
        state = PlayerScreenState.Loading(),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenErrorPreview() {
    PlayerScreen(
        state = PlayerScreenState.Error(errorMessage = "Failed to load audio"),
        onEvent = {}
    )
}


