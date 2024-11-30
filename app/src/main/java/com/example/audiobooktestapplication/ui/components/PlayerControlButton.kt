package com.example.audiobooktestapplication.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.audiobooktestapplication.R

@Composable
fun PlayerControlButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 36.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize)
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerControlButtonPreview() {
    PlayerControlButton(
        icon = painterResource(id = R.drawable.ic_play),
        contentDescription = "Play Button",
        onClick = {},
        iconSize = 24.dp,
        buttonSize = 36.dp
    )
}