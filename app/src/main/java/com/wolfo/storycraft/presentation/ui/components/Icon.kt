package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.wolfo.storycraft.R

@Composable
fun AnimatedAppLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Icon(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Loading Logo",
        modifier = modifier.graphicsLayer {
            rotationY = rotation
        },
        tint = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
fun AnimatedAppLogoPreview() {
    AnimatedAppLogo()
}